package core.data.temp

import core.domain.temp.TempFileItem
import core.domain.temp.TempFileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlin.random.Random
import kotlin.time.Clock

class TempFileRepositoryImpl(
    private val dirProvider: TempStorageDirProvider,
    private val fileOps: TempFileOps
) : TempFileRepository {

    private val index = TempFileIndex(dirProvider.getPath(), fileOps)

    override suspend fun saveBytes(byteArray: ByteArray, fileExtension: String): TempFileItem = withContext(Dispatchers.IO) {
        val ext = fileExtension.trimStart('.')
        val id = "file_${Clock.System.now().toEpochMilliseconds()}_${Random.nextInt(0, Int.MAX_VALUE)}"
        val fileName = "$id.$ext"
        val basePath = dirProvider.getPath()
        fileOps.ensureDir(basePath)
        val fullPath = "$basePath/$fileName"
        fileOps.writeFile(fullPath, byteArray)
        val item = TempFileItem(id = id, fileName = fileName)
        val entries = index.read().toMutableList()
        entries.add(index.toEntry(item))
        index.write(entries)
        item
    }

    override suspend fun loadAll(): List<TempFileItem> = withContext(Dispatchers.IO) {
        index.read().map { index.toTempFileItem(it) }
    }

    override suspend fun getCount(): Int = withContext(Dispatchers.IO) {
        index.read().size
    }

    override suspend fun loadBytes(item: TempFileItem): ByteArray? = withContext(Dispatchers.IO) {
        val fullPath = "${dirProvider.getPath()}/${item.fileName}"
        fileOps.readFile(fullPath)
    }

    override suspend fun delete(itemId: String) = withContext(Dispatchers.IO) {
        val entries = index.read().toMutableList()
        val entry = entries.find { it.id == itemId } ?: return@withContext
        val fullPath = "${dirProvider.getPath()}/${entry.fileName}"
        if (fileOps.fileExists(fullPath)) {
            fileOps.deleteFile(fullPath)
        }
        entries.removeAll { it.id == itemId }
        index.write(entries)
    }
}
