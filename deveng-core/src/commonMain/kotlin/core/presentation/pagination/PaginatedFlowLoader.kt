package core.presentation.pagination

import core.presentation.pagination.model.PageResult
import core.presentation.pagination.model.PaginatedListState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PaginatedFlowLoader<Key, Item>(
    private val initialKey: Key,
    private val scope: CoroutineScope,
    private val pageSize: Int,
    private var pageSource: suspend (key: Key, size: Int) -> PageResult<Item>,
    private val getNextKey: (Key, List<Item>) -> Key,
    private val onSuccess: ((List<Item>) -> Unit)? = null,
    private val onError: ((Throwable) -> Unit)? = null
) {
    private val _state = MutableStateFlow(PaginatedListState<Item>())
    val state: StateFlow<PaginatedListState<Item>> = _state

    private var currentKey: Key = initialKey
    private var isLoading = false
    private var currentJob: Job? = null

    /**
     * Reloads from the beginning using the SAME pageSource.
     * Use for full refresh or pull-to-refresh.
     */
    fun reset(newInitialKey: Key = initialKey) {
        currentJob?.cancel()
        currentKey = newInitialKey
        isLoading = false
        _state.value = PaginatedListState()
        loadInitialPage()
    }

    /**
     * Updates the underlying pageSource (e.g. same source with different filters,
     * queries or and entirely new sources)
     * and optionally triggers an immediate reload.
     */
    fun updatePageSource(
        newInitialKey: Key,
        newPageSource: suspend (key: Key, size: Int) -> PageResult<Item>,
        reload: Boolean = true
    ) {
        currentJob?.cancel()
        pageSource = newPageSource
        currentKey = newInitialKey
        isLoading = false
        _state.value = PaginatedListState()

        if (reload) {
            loadInitialPage()
        }
    }

    /**
     * Loads the first page. Safe to call multiple times.
     */
    fun loadInitialPage() {
        if (isLoading) return
        isLoading = true

        _state.update { it.copy(isInitialLoad = true, isError = false) }

        currentJob = scope.launch {
            try {
                val result = pageSource(currentKey, pageSize)
                val items = result.items
                val hasNext = result.hasNextPage ?: (items.size == pageSize)

                currentKey = getNextKey(currentKey, items)
                onSuccess?.invoke(items)

                _state.value = PaginatedListState(
                    items = items,
                    isInitialLoad = false,
                    isNextPageLoading = false,
                    hasNextPage = hasNext,
                    hasLoadedBefore = true,
                    isError = false
                )
            } catch (e: Throwable) {
                onError?.invoke(e)
                _state.update { it.copy(isInitialLoad = false, isError = true) }
                throw e
            } finally {
                isLoading = false
            }
        }
    }

    /**
     * Loads the next page and appends it to current items.
     */
    fun loadNextPage() {
        val currentState = _state.value
        if (isLoading || !currentState.hasNextPage) return

        isLoading = true
        _state.update { it.copy(isNextPageLoading = true, isError = false) }

        currentJob = scope.launch {
            try {
                val result = pageSource(currentKey, pageSize)
                val newItems = result.items
                val hasNext = result.hasNextPage ?: (newItems.size == pageSize)

                currentKey = getNextKey(currentKey, newItems)
                onSuccess?.invoke(newItems)

                _state.update {
                    it.copy(
                        items = it.items + newItems,
                        isNextPageLoading = false,
                        hasNextPage = hasNext,
                        hasLoadedBefore = true,
                        isError = false
                    )
                }
            } catch (e: Throwable) {
                onError?.invoke(e)
                _state.update { it.copy(isNextPageLoading = false, isError = true) }
                throw e
            } finally {
                isLoading = false
            }
        }
    }
}