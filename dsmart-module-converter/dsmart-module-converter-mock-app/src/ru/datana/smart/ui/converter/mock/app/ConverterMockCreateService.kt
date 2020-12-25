package ru.datana.smart.ui.converter.mock.app

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.http.content.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import ru.datana.smart.converter.transport.meta.models.*
import ru.datana.smart.logger.datanaLogger
import ru.datana.smart.ui.converter.mock.app.ConverterMockContext.Companion.EMPTY_FILE
import ru.datana.smart.ui.converter.mock.app.models.ConverterCaseSaveResponse
import ru.datana.smart.ui.converter.mock.app.models.ConverterMockError
import java.io.File
import java.io.InputStream
import java.io.OutputStream

class ConverterMockCreateService(
    val pathToCatalog: String = ""
) {

    private val logger = datanaLogger(this::class.java)
    private val objectMapper: ObjectMapper = jacksonObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);

    suspend fun exec(context: ConverterMockContext) {
        logger.info("request body: {}", objs = arrayOf(context.requestToSave))
        try {
            // Информация о кейсе
            handleCase(context)
            // Загрузка файлов
            handleUploads(context)
            // Сохранение meta.json
            handleMeltInfo(context)
            context.status = ConverterMockContext.Statuses.OK
        } catch (e: Throwable) {
            logger.error("Save melt info file failure: {}", objs = arrayOf(e))
            context.status = ConverterMockContext.Statuses.ERROR
            context.errors.add(e)
        }
        context.responseToSave = ConverterCaseSaveResponse(
            caseId = context.caseId,
            status = if (context.status == ConverterMockContext.Statuses.OK) ConverterCaseSaveResponse.Statuses.OK
            else ConverterCaseSaveResponse.Statuses.ERROR,
            errors = context.errors.map { ConverterMockError(message = it.message) }
        )
    }

    private suspend fun uploadFile(caseDir: File, filePart: PartData.FileItem): File {
        val fileName = filePart.originalFileName ?: return EMPTY_FILE
        val file = File(caseDir, fileName)
        filePart.streamProvider().use { its -> file.outputStream().buffered().use { its.copyToSuspend(it) } }
        filePart.dispose()
        return file
    }

    private suspend fun handleUploads(context: ConverterMockContext) {
        context.requestToSave.fileVideo?.let {
            context.fileVideoSaved = uploadFile(context.caseDir, it)
        }
        context.requestToSave.selsynJson?.let {
            context.selsynJsonSaved = uploadFile(context.caseDir, it)
        }
        context.requestToSave.slagRateJson?.let {
            context.slagRateJsonSaved = uploadFile(context.caseDir, it)
        }
    }

    private suspend fun handleCase(context: ConverterMockContext) {
        val caseId = context.requestToSave.caseId ?: throw RuntimeException("CaseId must be set")
        val caseDir = File("$pathToCatalog/case-$caseId")
        context.caseId = caseId
        context.caseDir = caseDir
        caseDir.mkdirs()
    }

    private suspend fun handleMeltInfo(context: ConverterMockContext) {
        val requestToSave = context.requestToSave
        context.meltInfo = ConverterMeltInfo(
            steelGrade = requestToSave.steelGrade,
            meltNumber = requestToSave.meltNumber,
            crewNumber = requestToSave.crewNumber,
            shiftNumber = requestToSave.shiftNumber,
            mode = ConverterMeltInfo.Mode.EMULATION,
            devices = ConverterMeltDevices(
                converter = ConverterDevicesConverter(
                    id = requestToSave.converterId,
                    name = requestToSave.converterName,
//                    deviceType = ConverterDevicesConverter::class.simpleName,
                    type = ConverterDeviceType.DEVICE
                ),
                irCamera = ConverterDevicesIrCamera(
                    id = requestToSave.irCameraId,
                    name = requestToSave.irCameraName,
//                    deviceType = ConverterDevicesIrCamera::class.simpleName,
                    uri = if (context.fileVideoSaved == EMPTY_FILE) null
                    else "${context.fileVideoSaved.parentFile.name}/${context.fileVideoSaved.name}",
                    type = ConverterDeviceType.FILE
                ),
                selsyn = ConverterDevicesSelsyn(
                    id = requestToSave.selsynId,
                    name = requestToSave.selsynName,
//                    deviceType = ConverterDevicesSelsyn::class.simpleName,
                    uri = if (context.selsynJsonSaved == EMPTY_FILE) null
                    else "${context.caseDir.name}/${context.selsynJsonSaved.name}",
                    type = ConverterDeviceType.FILE
                ),
                slagRate = ConverterDevicesSlagRate(
                    id = requestToSave.slagRateDeviceId,
                    name = requestToSave.slagRateDeviceName,
//                    deviceType = ConverterDevicesSlagRate::class.simpleName,
                    uri = if (context.slagRateJsonSaved == EMPTY_FILE) null
                    else "${context.caseDir.name}/${context.slagRateJsonSaved.name}",
                    type = ConverterDeviceType.FILE
                ),
            )
        )
        val metaFile = File(context.caseDir, "meta.json")
        objectMapper.writeValue(metaFile, context.meltInfo)
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
