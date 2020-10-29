package ru.datana.smart.ui.converter.mock.app

import io.ktor.http.content.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import ru.datana.smart.logger.datanaLogger
import java.io.File
import java.io.InputStream
import java.io.OutputStream

/*
1. Нужно объединить с create
2. Принимать данные одним скопом https://stackoverflow.com/questions/62823689/check-whether-all-parameter-exist-or-not-in-multipart-request-body-with-ktor
 */

class ConverterMockUploadService(
    val pathToCatalog: String = ""
) {

    private val logger = datanaLogger(this::class.java)

    fun exec(context: ConverterMockContext) {
        var newCaseFolderName: String = ""
        var fileName: String = "default-video-file-name"

        // Processes each part of the multipart input content
        multipart.forEachPart { part ->
            if (part is PartData.FormItem) {
                if (part.name == "file_name") {
                    logger.debug(" --- file_name: {}", objs = arrayOf(part.value))
                    fileName = part.value
                } else if (part.name == "new_case_folder_name") {
                    logger.debug(" --- new_case_folder_name: {}", objs = arrayOf(part.value))
                    newCaseFolderName = part.value
                }
            } else if (part is PartData.FileItem) {
                val uploadPath = caseCatalogDir.absolutePath + File.separatorChar + newCaseFolderName
                logger.debug(" --- fileDir: {}", objs = arrayOf(uploadPath))
                val ext = File(part.originalFileName).extension
                val file = File(uploadPath, "$fileName.$ext")
                part.streamProvider().use { its -> file.outputStream().buffered().use { its.copyToSuspend(it) } }
            }
            part.dispose()
        }
        context.status = ConverterMockContext.Statuses.OK
    }
}

/**
 * Utility boilerplate method that suspending,
 * copies a [this] [InputStream] into an [out] [OutputStream] in a separate thread.
 *
 * [bufferSize] and [yieldSize] allows to control how and when the suspending is performed.
 * The [dispatcher] allows to specify where will be this executed (for example a specific thread pool).
 */
suspend fun InputStream.copyToSuspend(
    out: OutputStream,
    bufferSize: Int = DEFAULT_BUFFER_SIZE,
    yieldSize: Int = 4 * 1024 * 1024,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
): Long {
    return withContext(dispatcher) {
        val buffer = ByteArray(bufferSize)
        var bytesCopied = 0L
        var bytesAfterYield = 0L
        while (true) {
            val bytes = read(buffer).takeIf { it >= 0 } ?: break
            out.write(buffer, 0, bytes)
            if (bytesAfterYield >= yieldSize) {
                yield()
                bytesAfterYield %= yieldSize
            }
            bytesCopied += bytes
            bytesAfterYield += bytes
        }
        return@withContext bytesCopied
    }
}
