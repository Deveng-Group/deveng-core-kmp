package core.presentation.component.mediaviewer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.zIndex
import core.presentation.component.mediaviewer.internal.SwipeToDismissBox
import core.presentation.component.mediaviewer.internal.ViewerPage
import core.presentation.component.mediaviewer.zoom.ZoomableConfig
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import androidx.compose.runtime.LaunchedEffect

/**
 * Full-screen media viewer with horizontal paging, pinch-to-zoom, double-tap zoom,
 * and swipe-to-dismiss.
 *
 * @param items List of items to display. Can be URLs, URIs, or any model supported by [content].
 * @param modifier Modifier for the viewer container.
 * @param state State holder. Use [rememberMediaViewerState] to create.
 * @param zoomableConfig Zoom behavior configuration (min/max scale, double-tap scale).
 * @param pageSpacing Horizontal spacing between pages.
 * @param beyondViewportPageCount Number of pages to preload outside the visible viewport.
 * @param onPageChanged Called when the current page changes (not on initial composition).
 * @param onDismiss Called when swipe-to-dismiss gesture is triggered.
 * @param enableSwipeToDismiss Whether vertical swipe-to-dismiss is enabled.
 * @param dismissThreshold Fraction of screen height (0~1) required to trigger dismiss.
 * @param backgroundColor Background color. Alpha fades during swipe-to-dismiss.
 * @param topBar Optional overlay at the top. Receives (currentPage, totalPages).
 * @param bottomBar Optional overlay at the bottom. Receives (currentPage, totalPages).
 * @param indicator Optional page indicator above bottomBar. Receives (currentPage, totalPages).
 * @param content Optional custom renderer per page. Receives (page index, item).
 *   When null, displays [items] as images via Coil AsyncImage with zoom.
 *   When provided, wraps your composable with zoom gestures automatically.
 */
@Composable
fun MediaViewer(
    items: List<Any>,
    modifier: Modifier = Modifier,
    state: MediaViewerState = rememberMediaViewerState(pageCount = { items.size }),
    zoomableConfig: ZoomableConfig = ZoomableConfig(),
    pageSpacing: Dp = MediaViewerDefaults.PageSpacing,
    beyondViewportPageCount: Int = MediaViewerDefaults.BeyondViewportPageCount,
    pageModifier: ((page: Int) -> Modifier)? = null,
    isPageZoomEnabled: ((page: Int) -> Boolean)? = null,
    onPageChanged: ((page: Int) -> Unit)? = null,
    onDismiss: (() -> Unit)? = null,
    enableSwipeToDismiss: Boolean = true,
    dismissThreshold: Float = MediaViewerDefaults.DismissThreshold,
    backgroundColor: Color = MediaViewerDefaults.BackgroundColor,
    topBar: (@Composable (currentPage: Int, totalPages: Int) -> Unit)? = null,
    bottomBar: (@Composable (currentPage: Int, totalPages: Int) -> Unit)? = null,
    indicator: (@Composable (currentPage: Int, totalPages: Int) -> Unit)? = null,
    content: (@Composable (page: Int, item: Any) -> Unit)? = null,
) {
    if (items.isEmpty()) return

    var dismissProgress by remember { mutableFloatStateOf(0f) }
    val currentOnPageChanged by rememberUpdatedState(onPageChanged)

    LaunchedEffect(state.pagerState) {
        snapshotFlow { state.pagerState.currentPage }
            .distinctUntilChanged()
            .drop(1)
            .collect { page -> currentOnPageChanged?.invoke(page) }
    }

    LaunchedEffect(state.pagerState) {
        snapshotFlow { state.pagerState.settledPage }
            .distinctUntilChanged()
            .collect { state.isZoomed = false }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor.copy(alpha = 1f - dismissProgress)),
    ) {
        SwipeToDismissBox(
            enabled = enableSwipeToDismiss && !state.isZoomed,
            threshold = dismissThreshold,
            velocityThreshold = MediaViewerDefaults.DismissVelocityThreshold,
            onDismiss = {
                dismissProgress = 0f
                onDismiss?.invoke()
            },
            onProgressChanged = { dismissProgress = it },
            onDragging = { state.isDismissing = it },
        ) {
            HorizontalPager(
                state = state.pagerState,
                modifier = Modifier.fillMaxSize(),
                pageSpacing = pageSpacing,
                beyondViewportPageCount = beyondViewportPageCount,
                userScrollEnabled = true,
                key = { it },
            ) { page ->
                val item = items.getOrNull(page) ?: return@HorizontalPager
                ViewerPage(
                    page = page,
                    imageModel = item,
                    isCurrentPage = page == state.pagerState.settledPage,
                    viewerState = state,
                    zoomableConfig = zoomableConfig,
                    zoomEnabled = isPageZoomEnabled?.invoke(page) ?: true,
                    sharedModifier = pageModifier?.invoke(page) ?: Modifier,
                    onZoomChanged = { isZoomed ->
                        if (page == state.pagerState.settledPage) state.isZoomed = isZoomed
                    },
                    content = content,
                )
            }
        }

        topBar?.let { bar ->
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .zIndex(10f),
            ) {
                bar(state.currentPage, items.size)
            }
        }

        if (indicator != null || bottomBar != null) {
            Column(
                modifier = Modifier.align(Alignment.BottomCenter),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                indicator?.invoke(state.currentPage, items.size)
                bottomBar?.invoke(state.currentPage, items.size)
            }
        }
    }
}
