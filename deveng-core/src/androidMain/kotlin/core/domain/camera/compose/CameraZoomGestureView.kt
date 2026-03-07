package core.domain.camera.compose

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import core.domain.camera.controller.CameraController

/**
 * Transparent overlay View that handles pinch-to-zoom and double-tap using the system
 * gesture detectors so it is active immediately (avoids Compose pointerInput delay on Android).
 */
internal class CameraZoomGestureView(
    context: Context,
    private var controller: CameraController,
    private var onZoomChange: (Float) -> Unit,
    private var onDoubleTap: () -> Unit,
    private var onFocusPointTapped: (Float, Float) -> Unit,
) : View(context) {

    private val scaleGestureDetector = ScaleGestureDetector(
        context,
        object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val current = controller.getZoom()
                val max = controller.getMaxZoom().coerceAtLeast(1f)
                val newZoom = (current * detector.scaleFactor).coerceIn(1f, max)
                controller.setZoom(newZoom)
                onZoomChange(controller.getZoom())
                return true
            }
        },
    )

    private val gestureDetector = GestureDetector(
        context,
        object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                onDoubleTap()
                return true
            }

            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                val w = width.toFloat()
                val h = height.toFloat()
                if (w > 0f && h > 0f) {
                    val nx = (e.x / w).coerceIn(0f, 1f)
                    val ny = (e.y / h).coerceIn(0f, 1f)
                    controller.setFocusPoint(nx, ny)
                    onFocusPointTapped(nx, ny)
                }
                return true
            }
        },
    )

    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleGestureDetector.onTouchEvent(event)
        gestureDetector.onTouchEvent(event)
        return true
    }

    fun update(
        newController: CameraController,
        newOnZoomChange: (Float) -> Unit,
        newOnDoubleTap: () -> Unit,
        newOnFocusPointTapped: (Float, Float) -> Unit,
    ) {
        controller = newController
        onZoomChange = newOnZoomChange
        onDoubleTap = newOnDoubleTap
        onFocusPointTapped = newOnFocusPointTapped
    }
}
