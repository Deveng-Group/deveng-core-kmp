package core.presentation.component.lazyswipecards

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.SpringSpec
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.unit.IntSize
import kotlin.math.max
import kotlin.math.min

@Stable
class SwipeCardsState(
    initialSelectedItemIndex: Int = 0,
) {

    internal var itemProvider: SwipeCardsItemProvider? = null

    private val _selectedIndex = mutableIntStateOf(initialSelectedItemIndex)
    val selectedItemIndex: Int get() = _selectedIndex.value

    internal val offsetXAnimatable = Animatable(0f)
    val offsetX: Float get() = offsetXAnimatable.value
    val isAnimationRunning: Boolean get() = offsetXAnimatable.isRunning

    private val _width = mutableIntStateOf(1)
    val viewportWidth: Int get() = _width.value

    internal var bound: Float = 0f
        get() = max(viewportWidth.toFloat(), field)
        set(value) {
            field = value
            updateBounds()
        }

    var ratio: Float = 0f
        private set

    val swipingDirection: SwipeDirection
        get() = getSwipeDirection(offsetX)

    /**
     * Last direction the top card was swiped (by gesture or programmatic swipe).
     * Use for [animateBackSwipe] so the previous card returns from the correct side.
     */
    var lastSwipeDirection: SwipeDirection = SwipeDirection.RIGHT

    /**
     * Key of the last swiped card. Set when a card is swiped; used for [onRevert] callback.
     */
    var lastSwipedKey: Any? = null

    /** History of swipes (key, direction). Revert only allowed when last swipe was LEFT. */
    private val swipeHistory = mutableListOf<Pair<Any?, SwipeDirection>>()

    /** True when the last swipe was LEFT so revert can undo that card. */
    val canRevert: Boolean
        get() = swipeHistory.isNotEmpty() && swipeHistory.last().second == SwipeDirection.LEFT

    /** Pops the last swipe (key, direction). Call only when [canRevert] is true. */
    internal fun popLastSwipe(): Pair<Any?, SwipeDirection>? = swipeHistory.removeLastOrNull()

    /** Records a swipe for revert history. */
    internal fun pushSwipe(key: Any?, direction: SwipeDirection) {
        lastSwipedKey = key
        lastSwipeDirection = direction
        swipeHistory.add(key to direction)
    }

    fun snapTo(index: Int) {
        _selectedIndex.value = index
    }

    suspend fun animateSwipe(
        direction: SwipeDirection,
        animationSpec: AnimationSpec<Float> = SpringSpec(),
        initialVelocity: Float = 0f,
    ) {
        updateBounds()
        val targetOffsetX = if (direction == SwipeDirection.RIGHT) {
            offsetXAnimatable.upperBound!!
        } else {
            offsetXAnimatable.lowerBound!!
        }
        offsetXAnimatable.animateTo(
            targetValue = targetOffsetX,
            animationSpec = animationSpec,
            initialVelocity = initialVelocity,
        )
        itemProvider?.onSwiped(targetOffsetX)
        offsetXAnimatable.snapTo(0f)
    }

    suspend fun animateBackSwipe(
        direction: SwipeDirection,
        animationSpec: AnimationSpec<Float> = SpringSpec(),
        initialVelocity: Float = 0f,
    ) {
        if (selectedItemIndex == 0) {
            return
        }
        _selectedIndex.value--

        updateBounds()
        val initialOffsetX = if (direction == SwipeDirection.RIGHT) {
            offsetXAnimatable.upperBound!!
        } else {
            offsetXAnimatable.lowerBound!!
        }
        offsetXAnimatable.snapTo(initialOffsetX)
        offsetXAnimatable.animateTo(
            targetValue = 0f,
            animationSpec = animationSpec,
            initialVelocity = initialVelocity,
        )
    }

    internal fun updateRatio(swipeThreshold: Float) {
        ratio = calculateRatio(offsetX, viewportWidth, swipeThreshold)
    }

    internal fun selectNextItem() {
        _selectedIndex.value++
    }

    internal fun onSizeChanged(newSize: IntSize) {
        _width.value = max(1, newSize.width)
    }

    private fun updateBounds() {
        offsetXAnimatable.updateBounds(
            lowerBound = -bound,
            upperBound = bound,
        )
    }

    companion object {

        val Saver: Saver<SwipeCardsState, *> = Saver(
            save = { it.selectedItemIndex },
            restore = { SwipeCardsState(it) },
        )
    }
}

@Composable
internal fun SwipeCardsState.bind(
    isEndless: Boolean,
    visibleItemCount: Int,
    itemProvider: SwipeCardsItemProvider,
    cardComposable: @Composable (cardIndex: Int, content: @Composable () -> Unit) -> Unit,
) {
    DisposableEffect(itemProvider) {
        this@bind.itemProvider = itemProvider
        onDispose {
            this@bind.itemProvider = null
        }
    }

    val selectedIndex = selectedItemIndex
    val visible = if (isEndless) {
        visibleItemCount
    } else {
        min(visibleItemCount, itemProvider.itemCount - 1 - selectedIndex)
    }
    for (localIndex in visible downTo 0) {
        cardComposable.invoke(localIndex) {
            val itemIndex = if (isEndless) {
                (selectedIndex + localIndex) % itemProvider.itemCount
            } else {
                selectedIndex + localIndex
            }
            itemProvider.Item(itemIndex, itemProvider.getKey(itemIndex))
        }
    }
}

@Composable
fun rememberSwipeCardsState(
    initialSelectedItemIndex: Int = 0,
): SwipeCardsState {
    return rememberSaveable(
        saver = SwipeCardsState.Saver,
        key = initialSelectedItemIndex.toString()
    ) {
        SwipeCardsState(
            initialSelectedItemIndex = initialSelectedItemIndex,
        )
    }
}
