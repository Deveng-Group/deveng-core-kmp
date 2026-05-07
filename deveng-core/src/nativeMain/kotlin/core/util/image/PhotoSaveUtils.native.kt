package core.util.image

import core.domain.camera.utils.toNSData
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.UByteVar
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned
import platform.CoreFoundation.CFDataCreate
import platform.CoreFoundation.CFDataCreateMutable
import platform.CoreFoundation.CFDataGetBytePtr
import platform.CoreFoundation.CFDataGetLength
import platform.CoreFoundation.CFRelease
import platform.CoreFoundation.CFStringRef
import platform.CoreFoundation.kCFAllocatorDefault
import platform.CoreServices.kUTTypeJPEG
import platform.Foundation.NSArray
import platform.Foundation.NSDictionary
import platform.Foundation.NSFileManager
import platform.Foundation.NSMutableDictionary
import platform.Foundation.NSNumber
import platform.Foundation.NSString
import platform.Foundation.NSURL
import platform.Foundation.create
import platform.ImageIO.CGImageDestinationAddImageFromSource
import platform.ImageIO.CGImageDestinationCreateWithData
import platform.ImageIO.CGImageDestinationFinalize
import platform.ImageIO.CGImageSourceCopyPropertiesAtIndex
import platform.ImageIO.CGImageSourceCreateWithData
import platform.ImageIO.kCGImagePropertyGPSDictionary
import platform.ImageIO.kCGImagePropertyGPSLatitude
import platform.ImageIO.kCGImagePropertyGPSLatitudeRef
import platform.ImageIO.kCGImagePropertyGPSLongitude
import platform.ImageIO.kCGImagePropertyGPSLongitudeRef
import platform.Photos.PHAssetChangeRequest
import platform.Photos.PHPhotoLibrary
import platform.posix.memcpy

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
actual object PhotoSaveUtils {

    actual fun setApplicationContext(context: Any?) {}

    actual fun imageBytesWithNormalOrientation(imageBytes: ByteArray): ByteArray = imageBytes

    actual fun savePhoto(imageBytes: ByteArray, targetPath: String): SavePhotoResult = try {
        val nsData = imageBytes.toNSData()
        val parentPath = targetPath.substringBeforeLast("/", "")
        if (parentPath.isNotEmpty()) {
            NSFileManager.defaultManager.createDirectoryAtPath(
                parentPath,
                withIntermediateDirectories = true,
                attributes = null,
                error = null,
            )
        }
        val success = NSFileManager.defaultManager.createFileAtPath(targetPath, nsData, null)
        if (!success) {
            return SavePhotoResult.Error(Exception("createFileAtPath returned false"))
        }
        saveToPhotosLibrary(targetPath)
        SavePhotoResult.Success(targetPath)
    } catch (e: Exception) {
        SavePhotoResult.Error(e)
    }

    private fun saveToPhotosLibrary(filePath: String) {
        var saveError: String? = null
        val semaphore = platform.darwin.dispatch_semaphore_create(0)

        PHPhotoLibrary.sharedPhotoLibrary().performChanges(
            changeBlock = {
                PHAssetChangeRequest.creationRequestForAssetFromImageAtFileURL(
                    NSURL.fileURLWithPath(filePath),
                )
            },
            completionHandler = { success, error ->
                if (!success || error != null) {
                    saveError = error?.localizedDescription ?: "Failed to save to Photos"
                    platform.Foundation.NSLog("PhotoSaveUtils: Failed to save to Photos: $saveError")
                }
                platform.darwin.dispatch_semaphore_signal(semaphore)
            },
        )

        platform.darwin.dispatch_semaphore_wait(semaphore, platform.darwin.DISPATCH_TIME_FOREVER)
    }

    actual fun addLocationExif(
        imageBytes: ByteArray,
        latitude: Double,
        longitude: Double,
    ): ByteArray {
        if (imageBytes.isEmpty()) {
            return imageBytes
        }
        val inData = imageBytes.toCFData() ?: return imageBytes
        val source = CGImageSourceCreateWithData(inData, options = null)
        CFRelease(inData)
        if (source == null) {
            return imageBytes
        }
        val outMutableData = CFDataCreateMutable(allocator = kCFAllocatorDefault, capacity = 0)
        if (outMutableData == null) {
            CFRelease(source)
            return imageBytes
        }
        val dest = CGImageDestinationCreateWithData(
            data = outMutableData,
            type = kUTTypeJPEG,
            count = 1u,
            options = null,
        )
        if (dest == null) {
            CFRelease(outMutableData)
            CFRelease(source)
            return imageBytes
        }
        try {
            val propsRef = CGImageSourceCopyPropertiesAtIndex(
                isrc = source,
                index = 0u,
                options = null,
            ) ?: run {
                CFRelease(dest)
                CFRelease(outMutableData)
                CFRelease(source)
                return imageBytes
            }
            val base = propsRef as NSDictionary
            val metadata = (base.mutableCopy() as? NSMutableDictionary) ?: run {
                CFRelease(propsRef)
                CFRelease(dest)
                CFRelease(outMutableData)
                CFRelease(source)
                return imageBytes
            }
            CFRelease(propsRef)
            val gpsDict = NSMutableDictionary()
            gpsDict.setObject(
                anObject = NSNumber(double = kotlin.math.abs(latitude)),
                forKey = cfStringKey(kCGImagePropertyGPSLatitude),
            )
            gpsDict.setObject(
                anObject = nsRefString(if (latitude >= 0.0) "N" else "S"),
                forKey = cfStringKey(kCGImagePropertyGPSLatitudeRef),
            )
            gpsDict.setObject(
                anObject = NSNumber(double = kotlin.math.abs(longitude)),
                forKey = cfStringKey(kCGImagePropertyGPSLongitude),
            )
            gpsDict.setObject(
                anObject = nsRefString(if (longitude >= 0.0) "E" else "W"),
                forKey = cfStringKey(kCGImagePropertyGPSLongitudeRef),
            )
            metadata.setObject(
                anObject = gpsDict,
                forKey = cfStringKey(kCGImagePropertyGPSDictionary),
            )

            CGImageDestinationAddImageFromSource(
                idst = dest,
                isrc = source,
                index = 0u,
                properties = metadata as platform.CoreFoundation.CFDictionaryRef?,
            )
            if (!CGImageDestinationFinalize(idst = dest)) {
                return imageBytes
            }
            return cfDataToByteArray(outMutableData) ?: imageBytes
        } finally {
            CFRelease(dest)
            CFRelease(outMutableData)
            CFRelease(source)
        }
    }

    actual fun readLocationFromExif(imageBytes: ByteArray): Pair<Double, Double>? {
        if (imageBytes.isEmpty()) {
            return null
        }
        val inData = imageBytes.toCFData() ?: return null
        val source = CGImageSourceCreateWithData(inData, options = null)
        CFRelease(inData)
        if (source == null) {
            return null
        }
        return try {
            val props = CGImageSourceCopyPropertiesAtIndex(isrc = source, index = 0u, options = null)
                ?: return null
            try {
                val dict = props as NSDictionary
                val gps = dict.objectForKey(cfStringKey(kCGImagePropertyGPSDictionary)) as? NSDictionary
                    ?: return null
                readGpsLatLon(gps)
            } finally {
                CFRelease(props)
            }
        } finally {
            CFRelease(source)
        }
    }

    private fun ByteArray.toCFData(): platform.CoreFoundation.CFDataRef? = usePinned { pinned ->
        CFDataCreate(
            allocator = kCFAllocatorDefault,
            bytes = pinned.addressOf(0).reinterpret<UByteVar>(),
            length = size.convert(),
        )
    }

    private fun cfDataToByteArray(data: platform.CoreFoundation.CFDataRef?): ByteArray? {
        if (data == null) {
            return null
        }
        val len = CFDataGetLength(data).toInt()
        if (len <= 0) {
            return null
        }
        val ptr = CFDataGetBytePtr(data) ?: return null
        return ByteArray(len).also { out ->
            out.usePinned { pinned ->
                memcpy(pinned.addressOf(0), ptr, len.convert())
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun cfStringKey(ref: CFStringRef?): NSString = ref as NSString

    private fun nsRefString(s: String): NSString =
        NSString.create(string = s)

    private fun readGpsLatLon(gps: NSDictionary): Pair<Double, Double>? {
        val lat = readSignedGpsComponent(
            gps = gps,
            valueKey = kCGImagePropertyGPSLatitude,
            refKey = kCGImagePropertyGPSLatitudeRef,
            positiveRef = "N",
            negativeRef = "S",
        ) ?: return null
        val lon = readSignedGpsComponent(
            gps = gps,
            valueKey = kCGImagePropertyGPSLongitude,
            refKey = kCGImagePropertyGPSLongitudeRef,
            positiveRef = "E",
            negativeRef = "W",
        ) ?: return null
        return lat to lon
    }

    private fun readSignedGpsComponent(
        gps: NSDictionary,
        valueKey: CFStringRef?,
        refKey: CFStringRef?,
        positiveRef: String,
        negativeRef: String,
    ): Double? {
        val refObj = gps.objectForKey(cfStringKey(refKey)) ?: return null
        val ref = (refObj as? NSString)?.description ?: return null
        val value = gps.objectForKey(cfStringKey(valueKey)) ?: return null
        val magnitude = gpsValueToDecimalDegrees(value) ?: return null
        val signed = when (ref) {
            positiveRef -> kotlin.math.abs(magnitude)
            negativeRef -> -kotlin.math.abs(magnitude)
            else -> magnitude
        }
        return signed
    }

    private fun gpsValueToDecimalDegrees(value: Any?): Double? = when (value) {
        is NSNumber -> value.doubleValue
        is NSArray -> dmsArrayToDecimal(value)
        is NSString -> {
            val desc = value.description ?: return null
            rationalStringToDouble(desc)
        }
        else -> null
    }

    private fun dmsArrayToDecimal(arr: NSArray): Double? {
        val count = arr.count.toInt()
        if (count != 3) {
            return null
        }
        val d0 = gpsValueToDecimalDegrees(arr.objectAtIndex(0u)) ?: return null
        val d1 = gpsValueToDecimalDegrees(arr.objectAtIndex(1u)) ?: return null
        val d2 = gpsValueToDecimalDegrees(arr.objectAtIndex(2u)) ?: return null
        return d0 + d1 / 60.0 + d2 / 3600.0
    }

    private fun rationalStringToDouble(s: String): Double? {
        val parts = s.split("/")
        if (parts.size != 2) {
            return s.toDoubleOrNull()
        }
        val num = parts[0].toDoubleOrNull() ?: return null
        val den = parts[1].toDoubleOrNull() ?: return null
        if (den == 0.0) {
            return null
        }
        return num / den
    }
}
