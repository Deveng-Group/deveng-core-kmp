package core.util.multiplatform

import platform.Foundation.NSLocale
import platform.Foundation.NSURL
import platform.Foundation.preferredLanguages
import platform.UIKit.UIApplication
import platform.UIKit.UIDevice
import platform.UIKit.UIPasteboard

@Suppress(names = ["EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING"])
actual class MultiPlatformUtils {
    actual fun dialPhoneNumber(phoneNumber: String): Boolean {
        val url = NSURL.Companion.URLWithString("tel:$phoneNumber")
        return if (url != null && UIApplication.Companion.sharedApplication.canOpenURL(url)) {
            UIApplication.Companion.sharedApplication.openURL(
                url,
                options = emptyMap<Any?, Any>(),
                completionHandler = null
            )
            true
        } else {
            copyToClipBoard(phoneNumber)
            false
        }
    }

    actual fun copyToClipBoard(text: String) {
        UIPasteboard.Companion.generalPasteboard.string = text
    }

    actual fun openMapsWithLocation(latitude: Double, longitude: Double) {
        val urlString = "http://maps.apple.com/?ll=$latitude,$longitude"
        val url = NSURL(string = urlString)

        UIApplication.sharedApplication.openURL(url)
    }

    actual fun getPlatformConfig(): PlatformConfig {
        val languageCode = NSLocale.preferredLanguages.firstOrNull() as? String ?: "en"
        return PlatformConfig(
            platform = Platform.NATIVE,
            systemLanguage = languageCode.substringBefore("-"),
            uuid = UIDevice.currentDevice.identifierForVendor?.UUIDString ?: "unknown_ios_uid",
            deviceName = "${UIDevice.currentDevice.name} - ${UIDevice.currentDevice.systemVersion} - ${UIDevice.currentDevice.model}",
            packageVersionName = "0.0.0"
        )
    }
}