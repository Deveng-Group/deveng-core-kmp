package core.util.multiplatform

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.core.net.toUri

@Suppress(names = ["EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING"])
actual class MultiPlatformUtils(private val context: Context) {
    actual fun dialPhoneNumber(phoneNumber: String): Boolean {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = "tel:$phoneNumber".toUri()
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        val packageManager = context.packageManager
        val canHandleIntent = intent.resolveActivity(packageManager) != null

        return if (canHandleIntent) {
            context.startActivity(intent)
            true
        } else {
            copyToClipBoard(phoneNumber)
            false
        }
    }

    actual fun copyToClipBoard(text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Phone number", text)
        clipboard.setPrimaryClip(clip)
    }

    actual fun openMapsWithLocation(latitude: Double, longitude: Double) {
        val uriString = "geo:$latitude,$longitude?q=$latitude,$longitude"
        val mapIntent = Intent(Intent.ACTION_VIEW).apply {
            data = uriString.toUri()
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        context.startActivity(mapIntent)
    }

    @SuppressLint("HardwareIds")
    actual fun getPlatformConfig(): PlatformConfig {
        return PlatformConfig(
            platform = Platform.ANDROID,
            systemLanguage = android.content.res.Resources.getSystem().configuration.locales[0].language,
            uuid = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID),
            deviceName = "${Build.MANUFACTURER} - ${Build.VERSION.SDK_INT} - ${Build.MODEL}",
            packageVersionName = "${"DEVENGCORE.VERSION_NAME"} - ${"DEVENGCORE.VERSION_CODE"}",
        )
    }
}