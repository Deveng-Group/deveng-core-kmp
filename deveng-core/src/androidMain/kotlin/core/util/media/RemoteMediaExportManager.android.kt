package core.util.media

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File
import java.net.URL

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class RemoteMediaExportManager(
    private val context: Context,
) {
    private companion object {
        const val TAG = "RemoteMediaExportManager"
    }

    actual fun shareSingleFileFromUrl(
        fileUrl: String,
        fileName: String,
        mimeType: String,
    ): Boolean {
        if (fileUrl.isBlank()) {
            return false
        }

        return runCatching {
            val bytes = URL(fileUrl).openStream().use { inputStream ->
                inputStream.readBytes()
            }
            shareBytesInternal(
                fileName = fileName,
                mimeType = mimeType,
                fileBytes = bytes,
            )
        }.onFailure { throwable ->
            Log.e(TAG, "shareSingleFileFromUrl failed url=$fileUrl", throwable)
        }.getOrDefault(false)
    }

    actual fun shareMultipleFilesFromUrls(
        files: List<RemoteMediaFile>,
    ): Boolean {
        if (files.isEmpty()) {
            return false
        }

        return runCatching {
            val sharedDir = File(context.cacheDir, "shared_media").apply {
                mkdirs()
            }
            val authority = "${context.applicationContext.packageName}.provider"

            val uris = ArrayList<Uri>()
            files.forEachIndexed { index, remoteFile ->
                if (remoteFile.fileUrl.isBlank()) {
                    return@forEachIndexed
                }

                val bytes = URL(remoteFile.fileUrl).openStream().use { inputStream ->
                    inputStream.readBytes()
                }
                if (bytes.isEmpty()) {
                    return@forEachIndexed
                }

                val fallbackName = "shared_${System.currentTimeMillis()}_$index"
                val normalizedFileName = remoteFile.fileName.ifBlank { fallbackName }
                val tempFile = File(sharedDir, normalizedFileName).apply {
                    writeBytes(bytes)
                }

                val fileUri = FileProvider.getUriForFile(
                    context,
                    authority,
                    tempFile,
                )
                uris.add(fileUri)
            }

            if (uris.isEmpty()) {
                return false
            }

            val uniqueMimeTypes = files.map { it.mimeType }.toSet()
            val mimeType = if (uniqueMimeTypes.size == 1) {
                uniqueMimeTypes.first()
            } else {
                "*/*"
            }

            val sendIntent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                type = mimeType
                putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            val chooserIntent = Intent.createChooser(sendIntent, null).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooserIntent)
            true
        }.onFailure { throwable ->
            Log.e(TAG, "shareMultipleFilesFromUrls failed count=${files.size}", throwable)
        }.getOrDefault(false)
    }

    actual fun saveSingleFileFromUrl(
        fileUrl: String,
        fileName: String,
        mimeType: String,
    ): Boolean {
        if (fileUrl.isBlank()) {
            return false
        }
        return runCatching {
            val bytes = URL(fileUrl).openStream().use { inputStream ->
                inputStream.readBytes()
            }
            saveBytesToMediaStore(
                fileName = fileName,
                mimeType = mimeType,
                fileBytes = bytes,
            )
        }.onFailure { throwable ->
            Log.e(TAG, "saveSingleFileFromUrl failed url=$fileUrl", throwable)
        }.getOrDefault(false)
    }

    actual fun saveMultipleFilesFromUrls(
        files: List<RemoteMediaFile>,
    ): Int {
        if (files.isEmpty()) {
            return 0
        }
        return runCatching {
            var successfulSaveCount = 0
            files.forEach { remoteFile ->
                if (remoteFile.fileUrl.isBlank()) {
                    return@forEach
                }
                val bytes = URL(remoteFile.fileUrl).openStream().use { inputStream ->
                    inputStream.readBytes()
                }
                val isSaved = saveBytesToMediaStore(
                    fileName = remoteFile.fileName,
                    mimeType = remoteFile.mimeType,
                    fileBytes = bytes,
                )
                if (isSaved) {
                    successfulSaveCount++
                }
            }
            successfulSaveCount
        }.onFailure { throwable ->
            Log.e(TAG, "saveMultipleFilesFromUrls failed count=${files.size}", throwable)
        }.getOrDefault(0)
    }

    private fun shareBytesInternal(
        fileName: String,
        mimeType: String,
        fileBytes: ByteArray,
    ): Boolean {
        if (fileBytes.isEmpty()) {
            return false
        }

        val sharedDir = File(context.cacheDir, "shared_media").apply {
            mkdirs()
        }
        val normalizedFileName = fileName.ifBlank { "shared_${System.currentTimeMillis()}" }
        val tempFile = File(sharedDir, normalizedFileName).apply {
            writeBytes(fileBytes)
        }

        val authority = "${context.applicationContext.packageName}.provider"
        val fileUri = FileProvider.getUriForFile(
            context,
            authority,
            tempFile,
        )

        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, fileUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        val chooserIntent = Intent.createChooser(sendIntent, null).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        context.startActivity(chooserIntent)
        return true
    }

    private fun saveBytesToMediaStore(
        fileName: String,
        mimeType: String,
        fileBytes: ByteArray,
    ): Boolean {
        if (fileBytes.isEmpty()) {
            return false
        }

        val normalizedFileName = fileName.ifBlank { "media_${System.currentTimeMillis()}" }
        val relativePath = when {
            mimeType.startsWith("video/") -> Environment.DIRECTORY_MOVIES
            else -> Environment.DIRECTORY_PICTURES
        }

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, normalizedFileName)
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath)
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
        }

        val resolver = context.contentResolver
        val targetCollection = if (mimeType.startsWith("video/")) {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val itemUri = resolver.insert(targetCollection, contentValues) ?: return false
        return try {
            resolver.openOutputStream(itemUri)?.use { outputStream ->
                outputStream.write(fileBytes)
            } ?: return false

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val publishedValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.IS_PENDING, 0)
                }
                resolver.update(itemUri, publishedValues, null, null)
            }
            true
        } catch (_: Exception) {
            resolver.delete(itemUri, null, null)
            false
        }
    }
}
