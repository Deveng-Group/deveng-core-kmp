package core.util.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.media.MediaScannerConnection
import java.io.ByteArrayOutputStream
import java.io.File

actual object PhotoSaveUtils {

    @Volatile
    private var appContext: Context? = null

    actual fun setApplicationContext(context: Any?) {
        appContext = (context as? Context)?.applicationContext
    }

    actual fun imageBytesWithNormalOrientation(imageBytes: ByteArray): ByteArray = try {
        val tempFile = File.createTempFile("exif_orient", ".jpg")
        try {
            tempFile.writeBytes(imageBytes)
            val exif = ExifInterface(tempFile.absolutePath)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL,
            )
            if (orientation == ExifInterface.ORIENTATION_NORMAL ||
                orientation == ExifInterface.ORIENTATION_UNDEFINED
            ) {
                return imageBytes
            }
            val rotationAngle = when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                else -> 0f
            }
            if (rotationAngle == 0f) return imageBytes
            var bitmap = BitmapFactory.decodeFile(tempFile.absolutePath, null) ?: return imageBytes
            try {
                val matrix = Matrix().apply { postRotate(rotationAngle) }
                val rotated = Bitmap.createBitmap(
                    bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true,
                )
                bitmap.recycle()
                bitmap = rotated
                val out = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
                out.toByteArray()
            } finally {
                if (!bitmap.isRecycled) bitmap.recycle()
            }
        } finally {
            tempFile.delete()
        }
    } catch (e: Exception) {
        imageBytes
    }

    actual fun savePhoto(imageBytes: ByteArray, targetPath: String): SavePhotoResult = try {
        val file = File(targetPath)
        file.parentFile?.mkdirs()
        file.writeBytes(imageBytes)
        appContext?.let { ctx ->
            MediaScannerConnection.scanFile(
                ctx,
                arrayOf(targetPath),
                arrayOf("image/jpeg"),
                null,
            )
        }
        SavePhotoResult.Success(targetPath)
    } catch (e: Exception) {
        SavePhotoResult.Error(e)
    }

    actual fun addLocationExif(
        imageBytes: ByteArray,
        latitude: Double,
        longitude: Double,
    ): ByteArray = try {
        val tempFile = File.createTempFile("exif_location", ".jpg")
        try {
            tempFile.writeBytes(imageBytes)
            val exif = ExifInterface(tempFile.absolutePath)
            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, toDmsString(latitude))
            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, if (latitude >= 0) "N" else "S")
            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, toDmsString(longitude))
            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, if (longitude >= 0) "E" else "W")
            exif.saveAttributes()
            tempFile.readBytes()
        } finally {
            tempFile.delete()
        }
    } catch (e: Exception) {
        imageBytes
    }

    /**
     * Converts decimal degrees to EXIF DMS string: "degrees/1,minutes/1,seconds/1000"
     */
    private fun toDmsString(decimalDegrees: Double): String {
        val abs = kotlin.math.abs(decimalDegrees)
        val degrees = abs.toInt()
        val minutesDecimal = (abs - degrees) * 60
        val minutes = minutesDecimal.toInt()
        val seconds = (minutesDecimal - minutes) * 60
        val secondsRational = (seconds * 1000).toInt()
        return "${degrees}/1,${minutes}/1,$secondsRational/1000"
    }
}
