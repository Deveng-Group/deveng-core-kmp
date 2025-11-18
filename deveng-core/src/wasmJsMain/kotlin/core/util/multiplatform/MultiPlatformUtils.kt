package core.util.multiplatform

import core.data.keyvalue.DeviceInfoStorage
import kotlinx.browser.window
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class MultiPlatformUtils(
    private val deviceInfoStorage: DeviceInfoStorage
) {
    actual fun dialPhoneNumber(phoneNumber: String): Boolean {
        return if (isMobileBrowser()) {
            window.location.href = "tel:$phoneNumber"
            true
        } else {
            copyToClipBoard(phoneNumber)
            false
        }
    }

    actual fun copyToClipBoard(text: String) {
        window.navigator.clipboard.writeText(text)
    }

    private fun isMobileBrowser(): Boolean {
        val userAgent = window.navigator.userAgent.lowercase()
        return userAgent.contains("iphone") ||
            (userAgent.contains("android") && userAgent.contains("mobile"))
    }

    actual fun getPlatformConfig(): PlatformConfig {
        return PlatformConfig(
            platform = Platform.WEB,
            systemLanguage = window.navigator.language.substring(0, 2).lowercase(),
            uuid = getGeneratedDeviceId(),
            deviceName = window.navigator.userAgent,
            packageVersionName = "0.0.0"

        )
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun getGeneratedDeviceId(): String {
        val storedId = deviceInfoStorage.getGeneratedPlatformIdentifier()
        if (storedId != null) return storedId

        val newId = Uuid.random().toString()
        deviceInfoStorage.setGeneratedPlatformIdentifier(newId)
        return newId
    }


}