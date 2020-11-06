//package ru.datana.smart.ui.converter.mock.app
//
//import io.ktor.http.content.*
//import kotlinx.coroutines.CoroutineDispatcher
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//import kotlinx.coroutines.yield
//import ru.datana.smart.logger.datanaLogger
//import java.io.File
//import java.io.InputStream
//import java.io.OutputStream
//
//class ConverterMockUploadService(
//    val pathToCatalog: String = ""
//) {
//
//    private val logger = datanaLogger(this::class.java)
//
//    suspend fun exec(context: ConverterMockContext) {
//        val uploadPath = pathToCatalog + File.separatorChar + context.uploadDataModel.newCaseFolderName.value
//        logger.debug("uploadPath: {}", objs = arrayOf(uploadPath))
//        println(" --- uploadPath: " + uploadPath)
//        val ext = File(context.uploadDataModel.file.originalFileName).extension
//        val file = File(uploadPath, "${context.uploadDataModel.fileName.value}.$ext")
//        logger.debug("File absolute path: {}", objs = arrayOf(file.absolutePath))
//        println(" --- file.absolutePath: " + file.absolutePath)
//        context.uploadDataModel.file.streamProvider()
//            .use { input -> file.outputStream().buffered().use { output -> input.copyToSuspend(output) } }
//        logger.info("File successfully uploaded to path: {}", objs = arrayOf(file.absolutePath))
//
//        context.status = ConverterMockContext.Statuses.OK
//    }
//}
//
///**
// * Utility boilerplate method that suspending,
// * copies a [this] [InputStream] into an [out] [OutputStream] in a separate thread.
// *
// * [bufferSize] and [yieldSize] allows to control how and when the suspending is performed.
// * The [dispatcher] allows to specify where will be this executed (for example a specific thread pool).
// */
//suspend fun InputStream.copyToSuspend(
//    out: OutputStream,
//    bufferSize: Int = DEFAULT_BUFFER_SIZE,
//    yieldSize: Int = 4 * 1024 * 1024,
//    dispatcher: CoroutineDispatcher = Dispatchers.IO
//): Long {
//    return withContext(dispatcher) {
//        val buffer = ByteArray(bufferSize)
//        var bytesCopied = 0L
//        var bytesAfterYield = 0L
//        while (true) {
//            val bytes = read(buffer).takeIf { it >= 0 } ?: break
//            out.write(buffer, 0, bytes)
//            if (bytesAfterYield >= yieldSize) {
//                yield()
//                bytesAfterYield %= yieldSize
//            }
//            bytesCopied += bytes
//            bytesAfterYield += bytes
//        }
//        return@withContext bytesCopied
//    }
//}
