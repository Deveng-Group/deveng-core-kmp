package core.presentation.component

import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import core.presentation.pagination.model.PaginatedListState
import core.presentation.theme.CustomBlackColor
import core.presentation.theme.MediumTextStyle
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlin.math.abs


/**
 * A cross-platform, pagination-aware list component for Compose Multiplatform.
 *
 * It supports:
 * - Endless scrolling (auto "load more" when reaching top/bottom depending on layout mode)
 * - Retry failed page loads via pull gestures:
 *      - Normal mode: pull UP from bottom to retry
 *      - Reverse/chat mode: pull DOWN from top to retry
 * - Optional reversed layout behavior (chat style)
 * - LazyColumn key support for stable item reuse
 * - Fully customizable error and retry UI text through composable providers
 *
 * This component does NOT perform data loading itself.
 * It only exposes events:
 *  - [onScrollReachNextPageThreshold]         → triggered when user scrolls near the loading edge
 *  - [onSwipeAtListsEnd]    → triggered when user performs the retry gesture
 *
 * Pair it with a paging loader (e.g., PaginatedFlowLoader) to manage state.
 *
 * @param state
 * The current pagination UI state.
 *
 * @param onScrollReachNextPageThreshold
 * Triggered automatically when the scroll position reaches the pagination boundary.
 *
 * @param onSwipeAtListsEnd
 * Triggered by user retry gesture when an error exists.
 *
 * @param itemSlot
 * Composable that renders an item from the list.
 *
 * @param spaceBetweenItems
 * Vertical spacing in dp.
 *
 * @param horizontalPadding
 * Horizontal padding around the list in dp.
 *
 * @param listBottomPadding
 * Extra bottom padding in dp.
 *
 * @param modifier
 * Parent modifier.
 *
 * @param prefetchThreshold
 * How early to trigger loading of next page.
 *
 * @param isReverseLayout
 * Enables chat-like behavior (load-at-top, retry-at-top).
 *
 * @param itemKey
 * Provides stable keys for better LazyColumn performance.
 *
 * @param emptyListText
 * Composable lambda producing the "empty list" string.
 *
 * @param errorTextProvider
 * Composable lambda producing the error message string.
 *
 * @param pullToRetryText
 * Composable lambda producing the “pull to retry” hint.
 */
@Composable
fun <T> PaginatedListView(
    state: PaginatedListState<T>,
    onScrollReachNextPageThreshold: () -> Unit,
    onSwipeAtListsEnd: () -> Unit,
    itemSlot: @Composable (T) -> Unit,
    spaceBetweenItems: Int = 12,
    horizontalPadding: Int = 13,
    listBottomPadding: Int = 5,
    modifier: Modifier = Modifier,
    prefetchThreshold: Int = 10,
    isReverseLayout: Boolean = false,
    itemKey: ((T) -> Any)? = null,
    textStyle: TextStyle = MediumTextStyle().copy(
        fontSize = 16.sp,
        color = CustomBlackColor
    ),
    emptyListText: String? = null,
    errorTextProvider: @Composable ((Throwable?) -> String)? = null,
    pullToRetryText: String? = null
) {
    val listState = rememberLazyListState()

    // auto scroll to end in reverse mode
    var didAutoScrollToEnd by remember { mutableStateOf(false) }

    LaunchedEffect(isReverseLayout, state.items.size) {
        if (isReverseLayout && state.items.isNotEmpty() && !didAutoScrollToEnd) {
            listState.scrollToItem(state.items.lastIndex)
            didAutoScrollToEnd = true
        }
        if (!isReverseLayout && state.items.isEmpty()) {
            didAutoScrollToEnd = false
        }
    }

    // infinite scroll trigger
    LaunchedEffect(
        listState,
        state.items.size,
        state.hasNextPage,
        state.isNextPageLoading,
        state.error,
        isReverseLayout
    ) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val firstVisible = layoutInfo.visibleItemsInfo.firstOrNull()?.index ?: -1
            val lastVisible = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            firstVisible to lastVisible
        }
            .distinctUntilChanged()
            .collectLatest { (firstVisibleIndex, lastVisibleIndex) ->
                val totalItems = state.items.size
                if (totalItems == 0) return@collectLatest

                val shouldLoadMore = if (!isReverseLayout) {
                    val triggerIndex = (totalItems - prefetchThreshold).coerceAtLeast(0)
                    lastVisibleIndex >= triggerIndex
                } else {
                    val triggerIndex = (prefetchThreshold - 1).coerceAtLeast(0)
                    firstVisibleIndex in 0..triggerIndex
                }

                if (
                    shouldLoadMore &&
                    state.hasNextPage &&
                    !state.isNextPageLoading &&
                    state.error == null
                ) {
                    onScrollReachNextPageThreshold()
                }
            }
    }

    // retry gesture (bottom-up normal, top-down reverse) ----
    val isAtLoadEdge by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            if (!isReverseLayout) {
                val total = layoutInfo.totalItemsCount
                val last = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
                total > 0 && last >= total - 1
            } else {
                listState.firstVisibleItemIndex == 0 &&
                        listState.firstVisibleItemScrollOffset == 0
            }
        }
    }

    val retryEnabled by remember(state.hasNextPage, state.isNextPageLoading, state.error) {
        mutableStateOf(
            state.hasNextPage &&
                    state.error != null &&
                    !state.isNextPageLoading
        )
    }

    var retryPullDistancePx by remember { mutableStateOf(0f) }
    val triggerDistancePx = with(LocalDensity.current) { 60.dp.toPx() }

    val retryDragModifier = Modifier.pointerInput(retryEnabled, isAtLoadEdge, isReverseLayout) {
        detectVerticalDragGestures(
            onVerticalDrag = { change, dragAmount ->
                if (!retryEnabled || !isAtLoadEdge) return@detectVerticalDragGestures

                val isValidDrag = if (!isReverseLayout) dragAmount < 0 else dragAmount > 0
                if (isValidDrag) {
                    retryPullDistancePx += abs(dragAmount)
                    change.consume()
                }
            },
            onDragEnd = {
                if (retryEnabled && retryPullDistancePx >= triggerDistancePx) {
                    onSwipeAtListsEnd()
                }
                retryPullDistancePx = 0f
            },
            onDragCancel = {
                retryPullDistancePx = 0f
            }
        )
    }

    Box(
        modifier = modifier
            .then(retryDragModifier)
            .padding(horizontal = horizontalPadding.dp)
    ) {
        LazyColumn(
            state = listState,
            verticalArrangement = if (state.items.isEmpty()) {
                Arrangement.Center
            } else {
                Arrangement.spacedBy(spaceBetweenItems.dp)
            },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Helper function to add status items (loading, error, empty)
            fun addStatusItems() {
                if (state.isInitialLoad && state.items.isEmpty()) {
                    item { CircularProgressIndicator() }
                }

                if (state.isNextPageLoading && state.items.isNotEmpty()) {
                    item { CircularProgressIndicator() }
                }

                if (
                    state.items.isEmpty() &&
                    !state.isNextPageLoading &&
                    !state.isInitialLoad &&
                    state.error == null &&
                    state.hasLoadedBefore &&
                    emptyListText != null
                ) {
                    item {
                        Text(
                            modifier = Modifier.padding(horizontal = 75.dp),
                            text = emptyListText,
                            style = textStyle,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                state.error?.let { error ->
                    item {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            errorTextProvider?.let { provider ->
                                Text(
                                    modifier = Modifier.padding(horizontal = 75.dp),
                                    text = provider(error),
                                    style = textStyle,
                                    textAlign = TextAlign.Center
                                )
                            }

                            pullToRetryText?.let { text ->
                                Text(
                                    modifier = Modifier.padding(horizontal = 75.dp),
                                    text = text,
                                    style = textStyle,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            // Add status items before content in reverse layout
            if (isReverseLayout) {
                addStatusItems()
            }

            // List items
            if (itemKey == null) {
                items(state.items.size) { index ->
                    itemSlot(state.items[index])
                }
            } else {
                items(
                    items = state.items,
                    key = { item -> itemKey(item) }
                ) { item ->
                    itemSlot(item)
                }
            }

            // Add status items after content in normal layout
            if (!isReverseLayout) {
                addStatusItems()
            }

            item {
                Spacer(modifier = Modifier.height(listBottomPadding.dp))
            }
        }
    }
}
