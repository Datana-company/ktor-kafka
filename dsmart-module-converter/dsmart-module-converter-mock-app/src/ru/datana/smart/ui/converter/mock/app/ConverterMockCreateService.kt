package ru.datana.smart.ui.converter.mock.app

import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.http.*
import ru.datana.smart.logger.datanaLogger
import java.io.File

class ConverterMockCreateService(
    val pathToCatalog: String = ""
) {

    private val logger = datanaLogger(this::class.java)
    private val objectMapper = ObjectMapper()

    fun exec(context: ConverterMockContext) {
        logger.info("request body: {}", objs = arrayOf(context.requestToSave))
        val meltInfo = context.requestToSave.meltInfo ?: throw RuntimeException("MeltInfo must be set")
        val caseName = context.requestToSave.caseName ?: throw RuntimeException("Case name must be set")
        val dirName = "$pathToCatalog/$caseName"
        try {
            val caseDir = File(dirName)
            if (caseDir.mkdirs() && caseDir.exists()) {
                val caseJsonFile = File(caseDir.absolutePath, "meta.json")
                if (caseJsonFile.createNewFile()) {
                    objectMapper.writeValue(caseJsonFile, meltInfo)
                }
            }
        } catch (e: Throwable) {
            logger.error("Save melt info file failure: {}", objs = arrayOf(e))
            context.status = ConverterMockContext.Statuses.ERROR
            return
        }
        context.responseToSave = ConverterCaseSaveResponse(
            caseName = caseName,
            meltInfo = meltInfo
        )
        context.status = ConverterMockContext.Statuses.OK
    }
}
