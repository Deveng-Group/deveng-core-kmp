package core.util.multiplatform

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class MultiPlatformUtils {
    fun dialPhoneNumber(phoneNumber: String): Boolean
    fun copyToClipBoard(text: String)
    fun openMapsWithLocation(latitude: Double, longitude: Double)
    fun openUrl(url: String)
    fun getPlatformConfig(): PlatformConfig
    suspend fun getCurrentLocation(): Pair<Double, Double>?
}