package ru.datana.smart.ui.converter.mock.app

import ru.datana.smart.logger.datanaLogger
import ru.datana.smart.ui.converter.mock.app.models.ConverterCaseListModel
import ru.datana.smart.ui.converter.mock.app.models.ConverterCaseModel
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.streams.toList

class ConverterMockListService(
    val pathToCatalog: String = ""
) {

    private val logger = datanaLogger(this::class.java)

    fun exec(context: ConverterMockContext) {
        logger.info(" +++ GET /list")
        val absolutePathToCatalog = try {
            Paths.get(pathToCatalog).toAbsolutePath()
        } catch (e: Throwable) {
            logger.error("String \"{}\" cannot be converted to Path", objs = arrayOf(pathToCatalog))
            context.status = ConverterMockContext.Statuses.ERROR
            return
        }

        if (!Files.exists(absolutePathToCatalog)) {
            logger.error("Catalog path {} is not found", absolutePathToCatalog)
            context.status = ConverterMockContext.Statuses.ERROR
            return
        }
        if (!Files.isDirectory(absolutePathToCatalog)) {
            logger.error("\"{}\" must be a directory rather than a file", absolutePathToCatalog)
            context.status = ConverterMockContext.Statuses.ERROR
            return
        }
        context.responseData = ConverterCaseListModel(cases = Files.walk(
            absolutePathToCatalog,
            1
        ) // Смотрим только 1 уровень (т.е. не заходим в каталоги)
            .filter { item -> Files.isDirectory(item) && item.fileName.toString().startsWith("case-") } // Оставляем только каталоги начинающиеся с "case-"
            .map {
                ConverterCaseModel(
                    name = it.fileName.toString(),
                    dir = it.toString()
                )
            }
            .toList()
        )
        context.status = ConverterMockContext.Statuses.OK
    }
}
