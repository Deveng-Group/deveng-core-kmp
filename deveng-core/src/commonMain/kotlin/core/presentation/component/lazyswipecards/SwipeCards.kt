package core.presentation.component.lazyswipecards

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.SpringSpec
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ScaleFactor
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger
import core.presentation.component.CustomIconButton
import core.presentation.theme.LocalComponentTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

@Composable
fun SwipeCards(
    modifier: Modifier = Modifier,
    cardModifier: Modifier = Modifier,
    cardShape: Shape = RectangleShape,
    cardColor: Color = MaterialTheme.colorScheme.surface,
    cardContentColor: Color = contentColorFor(cardColor),
    cardTonalElevation: Dp = 0.dp,
    cardShadowElevation: Dp = 2.dp,
    cardBorder: BorderStroke? = null,
    scaleFactor: ScaleFactor = ScaleFactor(
        scaleX = 0.1f,
        scaleY = 0.1f,
    ),
    translateSize: Dp = 24.dp,
    rotateDegree: Float = 14f,
    /**
     * When false, cards behind the front one keep a fixed stack transform (they do not track drag ratio each frame).
     * Use with heavy full-screen content; you lose parallax “under-finger” scaling on rear cards while dragging.
     */
    stackTracksDragRatio: Boolean = true,
    /** Composed stack depth; use a small value (e.g. 4) for heavy card content (video/decoders). */
    visibleItemCount: Int = 4,
    contentPadding: PaddingValues = defaultContentPadding(
        translateSize = translateSize,
        visibleItemCount = visibleItemCount,
    ),
    swipeThreshold: Float = 0.5f,
    minRatioBound: Float = MAX_RATIO,
    animationSpec: AnimationSpec<Float> = SpringSpec(),
    isEndless: Boolean = false,
    state: SwipeCardsState = rememberSwipeCardsState(),
    showSwipeButtons: Boolean = false,
    negativeButtonIcon: DrawableResource? = null,
    positiveButtonIcon: DrawableResource? = null,
    revertButtonIcon: DrawableResource? = null,
    negativeButtonContentDescription: String = "Swipe left",
    positiveButtonContentDescription: String = "Swipe right",
    revertButtonContentDescription: String = "Revert",
    onAllItemsConsumed: (() -> Unit)? = null,
    /** Called with the swiped card's key (id). Use the key parameter in [SwipeCardsScope.items] for stable id. */
    onSwipeLeft: ((Any?) -> Unit)? = null,
    /** Called with the swiped card's key (id). Use the key parameter in [SwipeCardsScope.items] for stable id. */
    onSwipeRight: ((Any?) -> Unit)? = null,
    /** Called with the reverted card's key (id) after undo. */
    onRevert: ((Any?) -> Unit)? = null,
    /**
     * When non-null, the revert button is shown and clicking it invokes [onRevert] with this key.
     * Use when the list has changed (e.g. after committing a previous pending item) and internal
     * revert state was lost, so the parent can still drive revert visibility.
     */
    pendingRevertKey: Any? = null,
    /** When this changes, content is re-applied so item composables (e.g. isCurrentItem) use latest state. Pass e.g. pendingRevertKey. */
    contentSeedKey: Any? = null,
    content: SwipeCardsScope.() -> Unit,
) {
    val itemProvider = rememberSwipeCardsItemProvider(
        content = content,
        state = state,
        isEndless = isEndless,
        contentSeedKey = contentSeedKey,
        onAllItemsConsumed = onAllItemsConsumed,
        onSwipeLeft = onSwipeLeft,
        onSwipeRight = onSwipeRight,
    )
    // DEBUG: yerel composite core doğrulama — kaldırmadan önce kullanıcı onayı al.
    LaunchedEffect(Unit) {
        Logger.withTag("DevengCoreSwipeCards").i {
            "LOCAL core smoke: SwipeCards composed (yerel deveng-core-kmp / composite)"
        }
    }
    // Peek / onSwiping: do NOT snapshotFlow on every drag frame (Animatable updates flood the main thread).
    // During finger drag, we sample only after each batched snapTo in SwipePointer. During animateTo/decay,
    // isAnimationRunning is true and a narrow snapshotFlow runs; when animation ends we sync once more.
    val swipeThrRef = rememberUpdatedState(swipeThreshold)
    val itemProviderRef = rememberUpdatedState(itemProvider)
    val onDragOffsetApplied = remember(state) {
        {
            applySwipePeekSample(state, swipeThrRef.value, itemProviderRef.value)
        }
    }
    LaunchedEffect(Unit) {
        var prevRunning = false
        snapshotFlow { state.isAnimationRunning }
            .collect { running ->
                if (prevRunning && !running) {
                    applySwipePeekSample(state, swipeThrRef.value, itemProviderRef.value)
                }
                prevRunning = running
            }
    }
    LaunchedEffect(Unit) {
        snapshotFlow { state.isAnimationRunning }
            .flatMapLatest { running ->
                if (running) {
                    snapshotFlow { state.offsetX to state.viewportWidth }
                        .distinctUntilChanged { (ox1, w1), (ox2, w2) ->
                            if (w1 != w2) return@distinctUntilChanged false
                            val thr = swipeThrRef.value
                            val r1 = calculateRatio(ox1, w1, thr)
                            val r2 = calculateRatio(ox2, w2, thr)
                            (r1 * 100f).toInt() == (r2 * 100f).toInt()
                        }
                } else {
                    emptyFlow()
                }
            }
            .collect { (ox, w) ->
                val thr = swipeThrRef.value
                val r = calculateRatio(ox, w, thr)
                state.syncRatioForCallbacks(r)
                itemProviderRef.value.onSwiping(ox, r)
            }
    }

    val scope = rememberCoroutineScope()
    val swipeCardsTheme = LocalComponentTheme.current.swipeCards
    val layoutDensity = LocalDensity.current.density
    val useLightweightCard =
        cardShadowElevation == 0.dp && cardTonalElevation == 0.dp && cardBorder == null
    Box(
        modifier = modifier
            .onSizeChanged(state::onSizeChanged)
            .padding(contentPadding),
        contentAlignment = Alignment.Center,
    ) {
        state.bind(
            isEndless = isEndless,
            visibleItemCount = visibleItemCount,
            itemProvider = itemProvider,
        ) { cardIndex, content ->
            val stackModifier = Modifier
                .swipe(
                    cardIndex = cardIndex,
                    state = state,
                    rotateDegree = rotateDegree,
                    minRatioBound = minRatioBound,
                    swipeThreshold = swipeThreshold,
                    scaleFactor = scaleFactor,
                    translateSize = translateSize,
                    visibleItemCount = visibleItemCount,
                    layoutDensity = layoutDensity,
                    animationSpec = animationSpec,
                    onSwipe = itemProvider::onSwiped,
                    onDragOffsetApplied = onDragOffsetApplied,
                    stackTracksDragRatio = stackTracksDragRatio,
                ) then cardModifier
            if (useLightweightCard) {
                CompositionLocalProvider(LocalContentColor provides cardContentColor) {
                    Box(
                        modifier = stackModifier
                            .clip(cardShape)
                            .background(cardColor, cardShape),
                    ) {
                        content()
                    }
                }
            } else {
                Surface(
                    modifier = stackModifier,
                    shape = cardShape,
                    color = cardColor,
                    contentColor = cardContentColor,
                    tonalElevation = cardTonalElevation,
                    shadowElevation = cardShadowElevation,
                    border = cardBorder,
                    content = content,
                )
            }
        }
        if (showSwipeButtons && (negativeButtonIcon != null || revertButtonIcon != null || positiveButtonIcon != null)) {
            val allConsumed = !isEndless && state.selectedItemIndex >= itemProvider.itemCount
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (negativeButtonIcon != null) {
                    if (!allConsumed) {
                        CustomIconButton(
                            buttonSize = swipeCardsTheme.buttonSize,
                            backgroundColor = swipeCardsTheme.buttonBackgroundColor,
                            iconTint = swipeCardsTheme.buttonIconTint,
                            shadowElevation = swipeCardsTheme.buttonShadowElevation,
                            icon = negativeButtonIcon,
                            iconDescription = negativeButtonContentDescription,
                            onClick = {
                                state.lastSwipeDirection = SwipeDirection.LEFT
                                scope.launch {
                                    state.animateSwipe(SwipeDirection.LEFT)
                                }
                            }
                        )
                    } else {
                        Spacer(modifier = Modifier.size(swipeCardsTheme.buttonSize))
                    }
                }
                if (positiveButtonIcon != null) {
                    if (!allConsumed) {
                        CustomIconButton(
                            buttonSize = swipeCardsTheme.buttonSize,
                            backgroundColor = swipeCardsTheme.buttonBackgroundColor,
                            iconTint = swipeCardsTheme.buttonIconTint,
                            shadowElevation = swipeCardsTheme.buttonShadowElevation,
                            icon = positiveButtonIcon,
                            iconDescription = positiveButtonContentDescription,
                            onClick = {
                                state.lastSwipeDirection = SwipeDirection.RIGHT
                                scope.launch {
                                    state.animateSwipe(SwipeDirection.RIGHT)
                                }
                            }
                        )
                    } else {
                        Spacer(modifier = Modifier.size(swipeCardsTheme.buttonSize))
                    }
                }
                if (revertButtonIcon != null) {
                    if (state.canRevert || pendingRevertKey != null) {
                        CustomIconButton(
                            buttonSize = swipeCardsTheme.buttonSize,
                            backgroundColor = swipeCardsTheme.buttonBackgroundColor,
                            iconTint = swipeCardsTheme.buttonIconTint,
                            shadowElevation = swipeCardsTheme.buttonShadowElevation,
                            icon = revertButtonIcon,
                            iconDescription = revertButtonContentDescription,
                            onClick = {
                                if (pendingRevertKey != null) {
                                    revertPendingSwipe(
                                        state = state,
                                        scope = scope,
                                        pendingRevertKey = pendingRevertKey,
                                        onRevert = onRevert,
                                    )
                                } else {
                                    val last = state.popLastSwipe() ?: return@CustomIconButton
                                    scope.launch {
                                        state.animateBackSwipe(last.second)
                                        onRevert?.invoke(last.first)
                                    }
                                }
                            }
                        )
                    } else {
                        Spacer(modifier = Modifier.size(swipeCardsTheme.buttonSize))
                    }
                }
            }
        }
    }
}

/**
 * Handles revert when the host provides [pendingRevertKey] (pending left-swipe not yet committed).
 * If the library has the swipe in history, animates it back; otherwise snaps offset and selected index
 * to 0 so the reverted card becomes the current card, then notifies host via [onRevert].
 */
private fun revertPendingSwipe(
    state: SwipeCardsState,
    scope: CoroutineScope,
    pendingRevertKey: Any?,
    onRevert: ((Any?) -> Unit)?,
) {
    val last = state.popLastSwipe()
    scope.launch {
        if (last != null) {
            state.animateBackSwipe(last.second)
        } else {
            state.offsetXAnimatable.snapTo(0f)
            state.snapTo(0)
        }
        onRevert?.invoke(pendingRevertKey)
    }
}

private fun applySwipePeekSample(
    state: SwipeCardsState,
    swipeThreshold: Float,
    itemProvider: SwipeCardsItemProvider,
) {
    val ox = state.offsetX
    val w = state.viewportWidth
    val r = calculateRatio(ox, w, swipeThreshold)
    state.syncRatioForCallbacks(r)
    itemProvider.onSwiping(ox, r)
}

private fun defaultContentPadding(
    translateSize: Dp,
    visibleItemCount: Int,
): PaddingValues {
    val absTranslateSize = translateSize.value.absoluteValue.dp
    return PaddingValues(
        vertical = absTranslateSize * visibleItemCount,
        horizontal = absTranslateSize,
    )
}

internal fun calculateRatio(
    offsetX: Float,
    width: Int,
    swipeThreshold: Float,
): Float {
    return min(
        MAX_RATIO, max(
            -MAX_RATIO,
            offsetX / (width * swipeThreshold)
        )
    )
}

private const val MAX_RATIO = 1f
