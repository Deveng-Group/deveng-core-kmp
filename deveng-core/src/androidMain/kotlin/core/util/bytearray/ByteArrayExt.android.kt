package core.util.bytearray

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

actual fun ByteArray.toImageBitmap(): ImageBitmap {
    val bmp = BitmapFactory.decodeByteArray(this, 0, size)
    return bmp.asImageBitmap()
}