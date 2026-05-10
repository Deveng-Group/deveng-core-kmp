package core.domain.camera.utils

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.allocArrayOf
import kotlinx.cinterop.get
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.useContents
import kotlinx.cinterop.usePinned
import org.jetbrains.skia.Data
import org.jetbrains.skia.Image
import platform.CoreGraphics.CGImageGetHeight
import platform.CoreGraphics.CGImageGetWidth
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.NSData
import platform.Foundation.create
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIGraphicsBeginImageContextWithOptions
import platform.UIKit.UIGraphicsEndImageContext
import platform.UIKit.UIGraphicsGetImageFromCurrentImageContext
import platform.posix.memcpy
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

fun ImageBitmap.toByteArray(): ByteArray? {
    val skiaBitmap = this.asSkiaBitmap()
    val skiaImage: Image = Image.makeFromBitmap(skiaBitmap)

    val encodedData: Data? = skiaImage.encodeToData(quality = 100)
    return encodedData?.bytes
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
fun ByteArray.toNSData(): NSData = memScoped {
    NSData.create(
        bytes = allocArrayOf(this@toNSData),
        length = this@toNSData.size.toULong(),
    )
}

/**
 * Converts NSData to ByteArray with optional buffer reuse for better memory efficiency
 *
 * @param reuseBuffer Optional pre-allocated buffer to use if large enough
 * @return ByteArray containing the data
 */
@OptIn(ExperimentalForeignApi::class)
fun NSData.toByteArray(reuseBuffer: ByteArray? = null): ByteArray {
    val length = this.length.toInt()

    val buffer =
        if (reuseBuffer != null && reuseBuffer.size >= length) {
            reuseBuffer
        } else {
            ByteArray(length)
        }

    buffer.usePinned {
        memcpy(it.addressOf(0), this@toByteArray.bytes, this@toByteArray.length)
    }

    return buffer
}

@OptIn(ExperimentalForeignApi::class)
fun NSData.toByteArray(): ByteArray = toByteArray(null)

fun NSData.toUIImage() = UIImage(this)

/**
 * If the decoded JPEG exceeds [capWidth]×[capHeight] when measured as (min side, max side),
 * downscales (aspect preserved) so both sides fit within that box and re-encodes as JPEG.
 * Matches Android still-capture max short/long semantics for [Pair] caps like 1440×2560.
 */
@OptIn(ExperimentalForeignApi::class)
fun capNSDataJpegToMaxPhotoDimensions(data: NSData, capWidth: Int, capHeight: Int): NSData {
    if (capWidth <= 0 || capHeight <= 0) return data
    val shortCap = min(capWidth, capHeight)
    val longCap = max(capWidth, capHeight)
    val uiImage = data.toUIImage()
    val cg = uiImage.CGImage
    val pixelW: Int
    val pixelH: Int
    if (cg != null) {
        pixelW = CGImageGetWidth(cg).toInt()
        pixelH = CGImageGetHeight(cg).toInt()
    } else {
        pixelW = (uiImage.size.useContents { width } * uiImage.scale).roundToInt()
        pixelH = (uiImage.size.useContents { height } * uiImage.scale).roundToInt()
    }
    if (pixelW <= 0 || pixelH <= 0) return data
    val shortSide = min(pixelW, pixelH)
    val longSide = max(pixelW, pixelH)
    if (shortSide <= shortCap && longSide <= longCap) {
        return data
    }
    val scale = min(shortCap.toFloat() / shortSide, longCap.toFloat() / longSide).coerceAtMost(1f)
    val newW = max(1, (pixelW * scale).roundToInt())
    val newH = max(1, (pixelH * scale).roundToInt())
    UIGraphicsBeginImageContextWithOptions(CGSizeMake(newW.toDouble(), newH.toDouble()), false, 1.0)
    try {
        uiImage.drawInRect(CGRectMake(0.0, 0.0, newW.toDouble(), newH.toDouble()))
        val resized = UIGraphicsGetImageFromCurrentImageContext() ?: return data
        return UIImageJPEGRepresentation(resized, 0.92) ?: data
    } finally {
        UIGraphicsEndImageContext()
    }
}

/**
 * Redraws the UIImage with orientation transformations applied to the pixel data.
 * Fixes issues where EXIF orientation metadata doesn't match the actual pixels,
 * which can cause rotated images when re-encoded to JPEG/PNG.
 *
 * @return UIImage with orientation baked into pixels
 */
@OptIn(ExperimentalForeignApi::class)
fun UIImage.fixOrientation(): UIImage {
    // If image is already in correct orientation, return it as-is
    if (this.imageOrientation == platform.UIKit.UIImageOrientation.UIImageOrientationUp) {
        return this
    }

    // Get the actual display size (after orientation transform is applied)
    val width = this.size.useContents { this.width }
    val height = this.size.useContents { this.height }

    // Create a graphics context with the display size and draw the image
    // UIImage.drawInRect automatically applies the orientation transformation
    platform.UIKit.UIGraphicsBeginImageContextWithOptions(this.size, false, this.scale)
    this.drawInRect(platform.CoreGraphics.CGRectMake(0.0, 0.0, width, height))
    val normalizedImage = platform.UIKit.UIGraphicsGetImageFromCurrentImageContext()
    platform.UIKit.UIGraphicsEndImageContext()

    return normalizedImage ?: this
}

@OptIn(ExperimentalForeignApi::class)
fun UIImage.toByteArray(): ByteArray = run {
    val imageData = UIImageJPEGRepresentation(this, 1.0)
        ?: throw IllegalArgumentException("image data is null")
    val bytes = imageData.bytes ?: throw IllegalArgumentException("image bytes is null")
    val length = imageData.length

    val data: CPointer<ByteVar> = bytes.reinterpret()
    ByteArray(length.toInt()) { index -> data[index] }
}
