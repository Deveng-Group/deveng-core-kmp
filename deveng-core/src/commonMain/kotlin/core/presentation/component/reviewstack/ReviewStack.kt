package core.presentation.component.reviewstack

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.key
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import core.presentation.theme.LocalComponentTheme
import core.util.debouncedCombinedClickable
import kotlin.time.Clock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import core.presentation.theme.ReviewStackTheme
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

private data class ReviewStackUndoSession<T>(
    val id: Long,
    val item: T,
    val decision: ReviewDecision,
    /** Wall time when this undo banner was created; used so auto-dismiss survives slot reorder. */
    val openedAtEpochMillis: Long,
)

/** Quick fade when the user taps undo on the banner. */
private const val UndoBannerFadeOutMs = 320

/** Slower, more visible fade for timer dismiss, oldest-row eviction when a 3rd banner appears, and overflow ghost. */
private const val UndoBannerStackSlotFadeOutMs = 520

private const val MaxUndoBannerStack = 3
private val UndoBannerStackSpacing = 8.dp

/**
 * A stacked viewer for browsing items one at a time using arrow buttons, while collecting a
 * positive / negative review decision per item.
 *
 * When the user marks a decision, the front card animates toward the corresponding decision button
 * (slight rotation + scale-down + translation) while a semi-transparent green / red overlay fades
 * in over the card. The decision callback fires after the animation completes.
 *
 * @param items The items to review.
 * @param key Stable key for an item; used to persist its decision in [state].
 * @param modifier Modifier for the outer container.
 * @param state Component state; use [rememberReviewStackState].
 * @param previousIcon Drawable for the previous-arrow button.
 * @param nextIcon Drawable for the next-arrow button.
 * @param negativeIcon Drawable for the negative (reject) button.
 * @param positiveIcon Drawable for the positive (accept) button.
 * @param previousIconDescription Accessibility label for the previous button.
 * @param nextIconDescription Accessibility label for the next button.
 * @param negativeIconDescription Accessibility label for the negative button.
 * @param positiveIconDescription Accessibility label for the positive button.
 * @param autoAdvanceOnDecision When true (default), marking a decision moves to the next item.
 * @param expandCardArea When true, the card area fills the remaining vertical space inside [modifier]
 *        (the parent must have a bounded height, e.g. [Modifier.fillMaxSize]). When false (default),
 *        the card uses [ReviewStackTheme.cardAspectRatio] to size itself.
 * @param showDecisionCounts When true (default), each decision button shows the running count of
 *        positive / negative marks next to its icon. When false, the buttons render as icon-only
 *        (same circular shape as arrow buttons).
 * @param topBarPadding Optional override for the top bar padding inside the card area. When null,
 *        uses [ReviewStackTheme.topBarPadding].
 * @param onDecision Called when the user marks the front item, AFTER the exit animation completes.
 * @param undoMessage Optional message on the in-stack undo banner after a negative decision.
 *        Pass non-null (or set [undoLabel]) to enable undo banner support.
 * @param undoLabel Action label on the undo banner (e.g. "Undo"). Shown when undo is enabled.
 * @param onUndoDecision Called when the user taps undo on the banner before it auto-dismisses.
 * @param topEndContent Optional slot rendered on the end side of the top bar (e.g. an overflow menu).
 * @param itemContent Composable used to render each item card's content.
 */
@Composable
fun <T> ReviewStack(
    items: List<T>,
    key: (T) -> Any,
    modifier: Modifier = Modifier,
    state: ReviewStackState = rememberReviewStackState(),
    previousIcon: DrawableResource,
    nextIcon: DrawableResource,
    negativeIcon: DrawableResource,
    positiveIcon: DrawableResource,
    mirrorPreviousIcon: Boolean = false,
    previousIconDescription: String = "Previous",
    nextIconDescription: String = "Next",
    negativeIconDescription: String = "Reject",
    positiveIconDescription: String = "Accept",
    autoAdvanceOnDecision: Boolean = true,
    expandCardArea: Boolean = false,
    showDecisionCounts: Boolean = true,
    topBarPadding: PaddingValues? = null,
    onDecision: ((item: T, decision: ReviewDecision) -> Unit)? = null,
    undoMessage: String? = null,
    undoLabel: String? = null,
    onUndoDecision: ((item: T, decision: ReviewDecision) -> Unit)? = null,
    topEndContent: (@Composable () -> Unit)? = null,
    itemContent: @Composable (item: T) -> Unit,
) {
    val theme = LocalComponentTheme.current.reviewStack
    state.clampIndex(items.size)
    val itemCount = items.size
    val currentIndex = state.currentIndex
    val hasItems = itemCount > 0
    val canGoPrevious = hasItems && currentIndex > 0
    val canGoNext = hasItems && currentIndex < itemCount - 1
    val cardShape = RoundedCornerShape(theme.cardCornerRadius)

    // Position tracking (in root coordinates) for animating the front card toward a decision button.
    var cardCenter by remember { mutableStateOf<Offset?>(null) }
    var negButtonCenter by remember { mutableStateOf<Offset?>(null) }
    var posButtonCenter by remember { mutableStateOf<Offset?>(null) }

    // Animation state. `pendingDecision` is non-null while the exit animation runs.
    var pendingDecision by remember { mutableStateOf<ReviewDecision?>(null) }
    var pendingItem by remember { mutableStateOf<T?>(null) }

    val scaleAnim = remember { Animatable(1f) }
    val rotationAnim = remember { Animatable(0f) }
    val translationXAnim = remember { Animatable(0f) }
    val translationYAnim = remember { Animatable(0f) }
    val overlayAlphaAnim = remember { Animatable(0f) }
    val composableScope = rememberCoroutineScope()

    val onDecisionRef = rememberUpdatedState(onDecision)
    val onUndoDecisionRef = rememberUpdatedState(onUndoDecision)
    var undoBannerStack by remember { mutableStateOf<List<ReviewStackUndoSession<T>>>(emptyList()) }
    var evictingUndoSession by remember { mutableStateOf<ReviewStackUndoSession<T>?>(null) }
    val evictingAlphaAnim = remember { Animatable(1f) }
    var undoSessionId by remember { mutableStateOf(0L) }
    /** Oldest session id when stack reaches [MaxUndoBannerStack]; that row fades out immediately. */
    var forcedImmediateFadeSessionId by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(pendingDecision, pendingItem) {
        val decision = pendingDecision
        val item = pendingItem
        if (decision == null || item == null) return@LaunchedEffect
        val cardC = cardCenter
        val targetC = when (decision) {
            ReviewDecision.NEGATIVE -> negButtonCenter
            ReviewDecision.POSITIVE -> posButtonCenter
        }
        val translateX = if (cardC != null && targetC != null) targetC.x - cardC.x else 0f
        val translateY = if (cardC != null && targetC != null) targetC.y - cardC.y else 0f
        val rotationTarget = when (decision) {
            ReviewDecision.NEGATIVE -> -theme.exitRotationDegrees
            ReviewDecision.POSITIVE -> theme.exitRotationDegrees
        }

        // Phase 1: tilt, shrink slightly, and start overlay fade — card stays in place so user sees the rotation.
        val phase1Spec = tween<Float>(durationMillis = theme.decisionAnimationPhase1Ms)
        val phase1Scale = 1f - (1f - theme.exitScale) * 0.55f
        coroutineScope {
            launch { rotationAnim.animateTo(rotationTarget, phase1Spec) }
            launch { scaleAnim.animateTo(phase1Scale, phase1Spec) }
            launch { overlayAlphaAnim.animateTo(theme.overlayAlpha, phase1Spec) }
        }

        // Phase 2: fly toward the decision button and shrink.
        val phase2Spec = tween<Float>(durationMillis = theme.decisionAnimationPhase2Ms)
        coroutineScope {
            launch { translationXAnim.animateTo(translateX, phase2Spec) }
            launch { translationYAnim.animateTo(translateY, phase2Spec) }
            launch { scaleAnim.animateTo(theme.exitScale, phase2Spec) }
        }

        // Animation done — commit decision, advance, then snap back so the next card renders cleanly.
        val committedItem = item
        val committedDecision = decision
        state.setDecision(key(committedItem), committedDecision)
        onDecisionRef.value?.invoke(committedItem, committedDecision)
        if (autoAdvanceOnDecision) state.goNext(itemCount)
        translationXAnim.snapTo(0f)
        translationYAnim.snapTo(0f)
        scaleAnim.snapTo(1f)
        rotationAnim.snapTo(0f)
        overlayAlphaAnim.snapTo(0f)
        // Clear pending first so next decision can start immediately.
        pendingItem = null
        pendingDecision = null
        // In-stack undo banner(s) for negative decisions (when undo UI is configured). Max [MaxUndoBannerStack].
        if ((undoMessage != null || undoLabel != null) && committedDecision == ReviewDecision.NEGATIVE) {
            undoSessionId += 1L
            val openedAt = Clock.System.now().toEpochMilliseconds()
            val newSession = ReviewStackUndoSession(
                id = undoSessionId,
                item = committedItem,
                decision = committedDecision,
                openedAtEpochMillis = openedAt,
            )
            if (undoBannerStack.size < MaxUndoBannerStack) {
                undoBannerStack = undoBannerStack + (newSession as ReviewStackUndoSession<T>)
                if (undoBannerStack.size == MaxUndoBannerStack) {
                    // Third banner: keep all three visible; oldest row starts fading immediately (timer ignored).
                    forcedImmediateFadeSessionId = undoBannerStack.first().id
                }
            } else {
                forcedImmediateFadeSessionId = null
                val oldest = undoBannerStack.first()
                evictingUndoSession = oldest
                undoBannerStack = undoBannerStack.drop(1) + (newSession as ReviewStackUndoSession<T>)
                val evictedId = oldest.id
                launch {
                    evictingAlphaAnim.snapTo(1f)
                    evictingAlphaAnim.animateTo(
                        0f,
                        tween(
                            durationMillis = UndoBannerStackSlotFadeOutMs,
                            easing = FastOutSlowInEasing,
                        ),
                    )
                    if (evictingUndoSession?.id == evictedId) {
                        evictingUndoSession = null
                    }
                    evictingAlphaAnim.snapTo(1f)
                }
            }
        }
    }

    var undoEntryAnimating by remember { mutableStateOf(false) }
    val isAnimating = pendingDecision != null || undoEntryAnimating

    val bannerUndoPreFade: (suspend (ReviewStackUndoSession<T>) -> Unit)? =
        if (onUndoDecision != null && (undoMessage != null || undoLabel != null)) {
            { session ->
                executeBannerUndoPreFade(
                    session = session,
                    state = state,
                    itemKey = key,
                    onUndoDecision = onUndoDecision,
                    theme = theme,
                    cardCenter = cardCenter,
                    negButtonCenter = negButtonCenter,
                    translationXAnim = translationXAnim,
                    translationYAnim = translationYAnim,
                    scaleAnim = scaleAnim,
                    rotationAnim = rotationAnim,
                    overlayAlphaAnim = overlayAlphaAnim,
                    setUndoEntryAnimating = { undoEntryAnimating = it },
                )
            }
        } else {
            null
        }

    Box(modifier = modifier) {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = (
                if (expandCardArea) {
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
                } else {
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(theme.cardAspectRatio)
                }
                ).onGloballyPositioned { coords ->
                val bounds = coords.boundsInRoot()
                cardCenter = Offset(
                    x = (bounds.left + bounds.right) / 2f,
                    y = (bounds.top + bounds.bottom) / 2f,
                )
            },
        ) {
            val depth = theme.visibleStackDepth.coerceAtLeast(1)
            val visible = (itemCount - currentIndex).coerceAtMost(depth).coerceAtLeast(0)
            // Render rear cards first (highest local index) so the front card is on top.
            for (localIndex in visible - 1 downTo 0) {
                val itemIndex = currentIndex + localIndex
                val item = items[itemIndex]
                val isFront = localIndex == 0
                val baseScale = 1f - theme.stackScalePerLevel * localIndex
                val baseTranslateY = with(LocalDensity.current) {
                    (theme.stackTranslatePerLevel * localIndex).toPx()
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            if (isFront) {
                                val s = baseScale * scaleAnim.value
                                scaleX = s
                                scaleY = s
                                translationX = translationXAnim.value
                                translationY = baseTranslateY + translationYAnim.value
                                rotationZ = rotationAnim.value
                            } else {
                                scaleX = baseScale
                                scaleY = baseScale
                                translationY = baseTranslateY
                            }
                        }
                        .then(
                            if (theme.cardShadowElevation > 0.dp) {
                                Modifier.shadow(theme.cardShadowElevation, cardShape)
                            } else {
                                Modifier
                            },
                        )
                        .clip(cardShape)
                        .background(theme.cardColor),
                ) {
                    if (isFront) {
                        itemContent(item)
                        // Decision overlay (green for positive, red for negative).
                        val overlayColor = when (pendingDecision) {
                            ReviewDecision.POSITIVE -> theme.positiveOverlayColor
                            ReviewDecision.NEGATIVE -> theme.negativeOverlayColor
                            null -> theme.negativeOverlayColor
                        }
                        if (overlayAlphaAnim.value > 0f) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .graphicsLayer { alpha = overlayAlphaAnim.value }
                                    .background(overlayColor),
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .padding(topBarPadding ?: theme.topBarPadding),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = if (hasItems) "${currentIndex + 1} / $itemCount" else "0 / 0",
                    style = theme.indexIndicatorTextStyle,
                )
                Spacer(modifier = Modifier.weight(1f))
                if (topEndContent != null) {
                    topEndContent()
                }
            }

            if (undoBannerStack.isNotEmpty() || evictingUndoSession != null) {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = theme.undoBannerHorizontalPadding)
                        .padding(bottom = theme.undoBannerBottomPadding)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(UndoBannerStackSpacing, Alignment.Bottom),
                ) {
                    // Bottom-first order: overflow ghost (fading evicted row), then oldest → newest active rows.
                    evictingUndoSession?.let { evicted ->
                        key(evicted.id) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .graphicsLayer { alpha = evictingAlphaAnim.value },
                            ) {
                                ReviewStackUndoBanner(
                                    modifier = Modifier.fillMaxWidth(),
                                    message = undoMessage.orEmpty(),
                                    label = undoLabel.orEmpty(),
                                    onUndoClick = {},
                                    theme = theme,
                                    interactive = false,
                                )
                            }
                        }
                    }
                    if (undoBannerStack.isNotEmpty()) {
                        key(undoBannerStack[0].id) {
                            UndoBannerRow(
                                session = undoBannerStack[0],
                                theme = theme,
                                undoMessage = undoMessage.orEmpty(),
                                undoLabel = undoLabel.orEmpty(),
                                composableScope = composableScope,
                                modifier = Modifier.fillMaxWidth(),
                                immediateFadeOut = undoBannerStack[0].id == forcedImmediateFadeSessionId,
                                onRemoveFromStack = { id ->
                                    if (forcedImmediateFadeSessionId == id) {
                                        forcedImmediateFadeSessionId = null
                                    }
                                    undoBannerStack = undoBannerStack.filter { it.id != id }
                                },
                                onUndoDecisionRef = onUndoDecisionRef,
                                suspendOnUndoFromBanner = bannerUndoPreFade,
                            )
                        }
                    }
                    if (undoBannerStack.size >= 2) {
                        key(undoBannerStack[1].id) {
                            UndoBannerRow(
                                session = undoBannerStack[1],
                                theme = theme,
                                undoMessage = undoMessage.orEmpty(),
                                undoLabel = undoLabel.orEmpty(),
                                composableScope = composableScope,
                                modifier = Modifier.fillMaxWidth(),
                                immediateFadeOut = false,
                                onRemoveFromStack = { id ->
                                    if (forcedImmediateFadeSessionId == id) {
                                        forcedImmediateFadeSessionId = null
                                    }
                                    undoBannerStack = undoBannerStack.filter { it.id != id }
                                },
                                onUndoDecisionRef = onUndoDecisionRef,
                                suspendOnUndoFromBanner = bannerUndoPreFade,
                            )
                        }
                    }
                    if (undoBannerStack.size >= 3) {
                        key(undoBannerStack[2].id) {
                            UndoBannerRow(
                                session = undoBannerStack[2],
                                theme = theme,
                                undoMessage = undoMessage.orEmpty(),
                                undoLabel = undoLabel.orEmpty(),
                                composableScope = composableScope,
                                modifier = Modifier.fillMaxWidth(),
                                immediateFadeOut = false,
                                onRemoveFromStack = { id ->
                                    if (forcedImmediateFadeSessionId == id) {
                                        forcedImmediateFadeSessionId = null
                                    }
                                    undoBannerStack = undoBannerStack.filter { it.id != id }
                                },
                                onUndoDecisionRef = onUndoDecisionRef,
                                suspendOnUndoFromBanner = bannerUndoPreFade,
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(theme.controlsTopPadding))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ArrowButton(
                icon = previousIcon,
                contentDescription = previousIconDescription,
                enabled = canGoPrevious && !isAnimating,
                size = theme.arrowButtonSize,
                background = theme.arrowButtonBackgroundColor,
                borderColor = theme.arrowButtonBorderColor,
                iconTint = theme.arrowButtonIconTint,
                shadowElevation = theme.arrowButtonShadowElevation,
                mirrorIcon = mirrorPreviousIcon,
                onClick = { state.goPrevious() },
            )

            DecisionButton(
                icon = negativeIcon,
                contentDescription = negativeIconDescription,
                count = state.negativeCount,
                showCount = showDecisionCounts,
                enabled = hasItems && !isAnimating,
                size = theme.decisionButtonSize,
                shape = theme.decisionButtonShape,
                background = theme.decisionButtonBackgroundColor,
                borderColor = theme.negativeBorderColor,
                borderWidth = theme.decisionBorderWidth,
                iconTint = theme.negativeIconTint,
                countColor = theme.negativeCountColor,
                countTextStyle = theme.decisionCountTextStyle,
                contentSpacing = theme.decisionContentSpacing,
                onPositioned = { center -> negButtonCenter = center },
                onClick = {
                    if (!isAnimating && hasItems) {
                        pendingItem = items[currentIndex]
                        pendingDecision = ReviewDecision.NEGATIVE
                    }
                },
            )

            DecisionButton(
                icon = positiveIcon,
                contentDescription = positiveIconDescription,
                count = state.positiveCount,
                showCount = showDecisionCounts,
                enabled = hasItems && !isAnimating,
                size = theme.decisionButtonSize,
                shape = theme.decisionButtonShape,
                background = theme.decisionButtonBackgroundColor,
                borderColor = theme.positiveBorderColor,
                borderWidth = theme.decisionBorderWidth,
                iconTint = theme.positiveIconTint,
                countColor = theme.positiveCountColor,
                countTextStyle = theme.decisionCountTextStyle,
                contentSpacing = theme.decisionContentSpacing,
                leadingCount = true,
                onPositioned = { center -> posButtonCenter = center },
                onClick = {
                    if (!isAnimating && hasItems) {
                        pendingItem = items[currentIndex]
                        pendingDecision = ReviewDecision.POSITIVE
                    }
                },
            )

            ArrowButton(
                icon = nextIcon,
                contentDescription = nextIconDescription,
                enabled = canGoNext && !isAnimating,
                size = theme.arrowButtonSize,
                background = theme.arrowButtonBackgroundColor,
                borderColor = theme.arrowButtonBorderColor,
                iconTint = theme.arrowButtonIconTint,
                shadowElevation = theme.arrowButtonShadowElevation,
                onClick = { state.goNext(itemCount) },
            )
        }

        Spacer(modifier = Modifier.height(theme.controlsBottomPadding))
    } // Column
    } // Box
}

private suspend fun animateNegativeUndoReveal(
    theme: ReviewStackTheme,
    cardCenter: Offset?,
    negButtonCenter: Offset?,
    translationXAnim: Animatable<Float, AnimationVector1D>,
    translationYAnim: Animatable<Float, AnimationVector1D>,
    scaleAnim: Animatable<Float, AnimationVector1D>,
    rotationAnim: Animatable<Float, AnimationVector1D>,
    overlayAlphaAnim: Animatable<Float, AnimationVector1D>,
) {
    val cardC = cardCenter
    val targetC = negButtonCenter
    val translateX = if (cardC != null && targetC != null) targetC.x - cardC.x else 0f
    val translateY = if (cardC != null && targetC != null) targetC.y - cardC.y else 0f
    val rotationTarget = -theme.exitRotationDegrees
    val phase1Scale = 1f - (1f - theme.exitScale) * 0.55f
    translationXAnim.snapTo(translateX)
    translationYAnim.snapTo(translateY)
    scaleAnim.snapTo(theme.exitScale)
    rotationAnim.snapTo(rotationTarget)
    overlayAlphaAnim.snapTo(theme.overlayAlpha)
    val phase2Spec = tween<Float>(durationMillis = theme.decisionAnimationPhase2Ms)
    coroutineScope {
        launch { translationXAnim.animateTo(0f, phase2Spec) }
        launch { translationYAnim.animateTo(0f, phase2Spec) }
        launch { scaleAnim.animateTo(phase1Scale, phase2Spec) }
    }
    val phase1Spec = tween<Float>(durationMillis = theme.decisionAnimationPhase1Ms)
    coroutineScope {
        launch { rotationAnim.animateTo(0f, phase1Spec) }
        launch { scaleAnim.animateTo(1f, phase1Spec) }
        launch { overlayAlphaAnim.animateTo(0f, phase1Spec) }
    }
}

private suspend fun <T> executeBannerUndoPreFade(
    session: ReviewStackUndoSession<T>,
    state: ReviewStackState,
    itemKey: (T) -> Any,
    onUndoDecision: ((T, ReviewDecision) -> Unit)?,
    theme: ReviewStackTheme,
    cardCenter: Offset?,
    negButtonCenter: Offset?,
    translationXAnim: Animatable<Float, AnimationVector1D>,
    translationYAnim: Animatable<Float, AnimationVector1D>,
    scaleAnim: Animatable<Float, AnimationVector1D>,
    rotationAnim: Animatable<Float, AnimationVector1D>,
    overlayAlphaAnim: Animatable<Float, AnimationVector1D>,
    setUndoEntryAnimating: (Boolean) -> Unit,
) {
    setUndoEntryAnimating(true)
    try {
        onUndoDecision?.invoke(session.item, session.decision)
        state.clearDecision(itemKey(session.item))
        if (session.decision == ReviewDecision.NEGATIVE) {
            repeat(2) { withFrameNanos { } }
            animateNegativeUndoReveal(
                theme = theme,
                cardCenter = cardCenter,
                negButtonCenter = negButtonCenter,
                translationXAnim = translationXAnim,
                translationYAnim = translationYAnim,
                scaleAnim = scaleAnim,
                rotationAnim = rotationAnim,
                overlayAlphaAnim = overlayAlphaAnim,
            )
        }
    } finally {
        setUndoEntryAnimating(false)
    }
}

@Composable
private fun <T> UndoBannerRow(
    session: ReviewStackUndoSession<T>,
    theme: ReviewStackTheme,
    undoMessage: String,
    undoLabel: String,
    composableScope: CoroutineScope,
    onRemoveFromStack: (Long) -> Unit,
    onUndoDecisionRef: State<((T, ReviewDecision) -> Unit)?>,
    suspendOnUndoFromBanner: (suspend (ReviewStackUndoSession<T>) -> Unit)?,
    modifier: Modifier = Modifier,
    /** When true, skip the visible timer and fade this row out immediately (oldest row when a 3rd banner appears). */
    immediateFadeOut: Boolean = false,
) {
    val rowFadeAnim = remember(session.id) { Animatable(1f) }
    LaunchedEffect(session.id, session.openedAtEpochMillis, theme.undoBannerVisibleMs, immediateFadeOut) {
        if (immediateFadeOut) {
            rowFadeAnim.animateTo(
                0f,
                tween(
                    durationMillis = UndoBannerStackSlotFadeOutMs,
                    easing = FastOutSlowInEasing,
                ),
            )
            onRemoveFromStack(session.id)
            rowFadeAnim.snapTo(1f)
            return@LaunchedEffect
        }
        val visibleMs = theme.undoBannerVisibleMs.coerceAtLeast(250).toLong()
        val elapsed = Clock.System.now().toEpochMilliseconds() - session.openedAtEpochMillis
        delay((visibleMs - elapsed).coerceAtLeast(0L))
        rowFadeAnim.animateTo(
            0f,
            tween(
                durationMillis = UndoBannerStackSlotFadeOutMs,
                easing = FastOutSlowInEasing,
            ),
        )
        onRemoveFromStack(session.id)
        rowFadeAnim.snapTo(1f)
    }
    Box(modifier = modifier.graphicsLayer { alpha = rowFadeAnim.value }) {
        ReviewStackUndoBanner(
            modifier = Modifier.fillMaxWidth(),
            message = undoMessage,
            label = undoLabel,
            onUndoClick = {
                composableScope.launch {
                    val preFade = suspendOnUndoFromBanner
                    if (preFade != null) {
                        preFade(session)
                    } else {
                        onUndoDecisionRef.value?.invoke(session.item, session.decision)
                    }
                    rowFadeAnim.animateTo(0f, tween(durationMillis = UndoBannerFadeOutMs))
                    onRemoveFromStack(session.id)
                    rowFadeAnim.snapTo(1f)
                }
            },
            theme = theme,
        )
    }
}

@Composable
private fun ReviewStackUndoBanner(
    modifier: Modifier,
    message: String,
    label: String,
    onUndoClick: () -> Unit,
    theme: ReviewStackTheme,
    interactive: Boolean = true,
) {
    val shape = RoundedCornerShape(theme.undoBannerCornerRadius)
    val maxW = theme.undoBannerMaxWidth
    Box(
        modifier = modifier
            .fillMaxWidth()
            .then(if (maxW != null) Modifier.widthIn(max = maxW) else Modifier)
            .clip(shape)
            .background(theme.undoBannerContainerColor)
            .then(
                if (interactive) {
                    Modifier.clickable(onClick = onUndoClick, onClickLabel = label)
                } else {
                    Modifier
                },
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = if (message.isNotBlank()) {
                Arrangement.SpaceBetween
            } else {
                Arrangement.Center
            },
        ) {
            if (message.isNotBlank()) {
                Text(
                    text = message,
                    style = theme.undoBannerMessageTextStyle.copy(color = theme.undoBannerContentColor),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f).padding(end = 8.dp),
                )
            }
            Text(
                text = label,
                style = theme.undoBannerActionTextStyle.copy(color = theme.undoBannerActionColor),
            )
        }
    }
}

@Composable
private fun ArrowButton(
    icon: DrawableResource,
    contentDescription: String,
    enabled: Boolean,
    size: Dp,
    background: Color,
    borderColor: Color,
    iconTint: Color,
    shadowElevation: Dp,
    mirrorIcon: Boolean = false,
    onClick: () -> Unit,
) {
    val alpha = if (enabled) 1f else 0.4f
    Box(
        modifier = Modifier
            .size(size)
            .graphicsLayer { this.alpha = alpha }
            .then(
                if (shadowElevation > 0.dp) Modifier.shadow(shadowElevation, CircleShape) else Modifier,
            )
            .clip(CircleShape)
            .background(background, CircleShape)
            .border(width = 1.dp, color = borderColor, shape = CircleShape)
            .debouncedCombinedClickable(
                debounceMillis = 300L,
                shape = CircleShape,
            ) {
                if (enabled) onClick()
            },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = contentDescription,
            tint = iconTint,
            modifier = if (mirrorIcon) Modifier.graphicsLayer { scaleX = -1f } else Modifier,
        )
    }
}

@Composable
private fun DecisionButton(
    icon: DrawableResource,
    contentDescription: String,
    count: Int,
    showCount: Boolean,
    enabled: Boolean,
    size: Dp,
    shape: Shape,
    background: Color,
    borderColor: Color,
    borderWidth: Dp,
    iconTint: Color,
    countColor: Color,
    countTextStyle: TextStyle,
    contentSpacing: Dp,
    leadingCount: Boolean = false,
    onPositioned: (Offset) -> Unit,
    onClick: () -> Unit,
) {
    val alpha = if (enabled) 1f else 0.4f
    val effectiveShape = if (showCount) shape else CircleShape
    val containerModifier = Modifier
        .graphicsLayer { this.alpha = alpha }
        .then(if (!showCount) Modifier.size(size) else Modifier)
        .clip(effectiveShape)
        .background(background, effectiveShape)
        .border(width = borderWidth, color = borderColor, shape = effectiveShape)
        .onGloballyPositioned { coords ->
            val bounds = coords.boundsInRoot()
            onPositioned(
                Offset(
                    x = (bounds.left + bounds.right) / 2f,
                    y = (bounds.top + bounds.bottom) / 2f,
                ),
            )
        }
        .debouncedCombinedClickable(shape = effectiveShape) {
            if (enabled) onClick()
        }
    Box(
        modifier = containerModifier,
        contentAlignment = Alignment.Center,
    ) {
        if (!showCount) {
            Icon(
                painter = painterResource(icon),
                contentDescription = contentDescription,
                tint = iconTint,
            )
        } else {
            Row(
                modifier = Modifier
                    .height(size)
                    .padding(horizontal = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(contentSpacing, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (leadingCount) {
                    Text(
                        text = count.toString(),
                        style = countTextStyle.copy(color = countColor),
                    )
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = contentDescription,
                        tint = iconTint,
                    )
                } else {
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = contentDescription,
                        tint = iconTint,
                    )
                    Text(
                        text = count.toString(),
                        style = countTextStyle.copy(color = countColor),
                    )
                }
            }
        }
    }
}
