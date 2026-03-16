package core.presentation.component.lazyswipecards

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.SpringSpec
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ScaleFactor
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import core.presentation.component.CustomIconButton
import core.presentation.theme.LocalComponentTheme
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
    visibleItemCount: Int = 3,
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
    content: SwipeCardsScope.() -> Unit,
) {
    state.updateRatio(swipeThreshold = swipeThreshold)
    val itemProvider = rememberSwipeCardsItemProvider(
        content = content,
        state = state,
        isEndless = isEndless,
        onAllItemsConsumed = onAllItemsConsumed,
        onSwipeLeft = onSwipeLeft,
        onSwipeRight = onSwipeRight,
    )
    LaunchedEffect(state.ratio) {
        itemProvider.onSwiping(state.offsetX, state.ratio)
    }

    val scope = rememberCoroutineScope()
    val swipeCardsTheme = LocalComponentTheme.current.swipeCards
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
            Surface(
                modifier = Modifier
                    .swipe(
                        cardIndex = cardIndex,
                        state = state,
                        rotateDegree = rotateDegree,
                        minRatioBound = minRatioBound,
                        swipeThreshold = swipeThreshold,
                        scaleFactor = scaleFactor,
                        translateSize = translateSize,
                        visibleItemCount = visibleItemCount,
                        animationSpec = animationSpec,
                        onSwipe = itemProvider::onSwiped,
                    ) then cardModifier,
                shape = cardShape,
                color = cardColor,
                contentColor = cardContentColor,
                tonalElevation = cardTonalElevation,
                shadowElevation = cardShadowElevation,
                border = cardBorder,
                content = content,
            )
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
                                    onRevert?.invoke(pendingRevertKey)
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
