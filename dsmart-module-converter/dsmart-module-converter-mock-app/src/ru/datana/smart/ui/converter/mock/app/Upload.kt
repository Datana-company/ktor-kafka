package ru.datana.smart.ui.converter.mock.app

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.locations.post
import io.ktor.request.receiveMultipart
import io.ktor.response.respond
import io.ktor.routing.Route
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import java.io.File
import java.io.InputStream
import java.io.OutputStream

/**
 * Register [Upload] routes.
 */
fun Route.upload(caseCatalogDir: File) {

    /**
     * Registers a POST route for [Upload] that actually read the bits sent from the client.
     */
    post<Upload> {
        val multipart = call.receiveMultipart()
        var newCaseFolderName: String = ""
        var fileName: String = "default-video-file-name"

        // Processes each part of the multipart input content
        multipart.forEachPart { part ->
            if (part is PartData.FormItem) {
                if (part.name == "file_name") {
                    println("------------ file_name: " + part.value)
                    fileName = part.value
                } else if (part.name == "new_case_folder_name") {
                    println("------------ new_case_folder_name: " + part.value)
                    newCaseFolderName = part.value
                }
            } else if (part is PartData.FileItem) {
                val uploadPath = caseCatalogDir.absolutePath + File.separatorChar + newCaseFolderName
                println(" +++ fileDir: $uploadPath")
                val ext = File(part.originalFileName).extension
                val file = File(uploadPath, "$fileName.$ext")
                part.streamProvider().use { its -> file.outputStream().buffered().use { its.copyToSuspend(it) } }
            }
            part.dispose()
        }
        call.respond(HttpStatusCode.OK)
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
