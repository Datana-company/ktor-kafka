package ru.datana.smart.ui.converter.angle.app.cor.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.logger.datanaLogger
import ru.datana.smart.ui.converter.angle.app.cor.context.ConverterAngleContext
import ru.datana.smart.ui.converter.angle.app.cor.context.CorError
import ru.datana.smart.ui.converter.angle.app.cor.context.CorStatus
import ru.datana.smart.ui.converter.angle.app.main
import ru.datana.smart.ui.mlui.models.ConverterMeltInfo


object ConverterMetaHandler : IKonveyorHandler<ConverterAngleContext<String, String>> {

    private val logger = datanaLogger(ConverterMetaHandler::class.java)

    override suspend fun exec(context: ConverterAngleContext<String, String>, env: IKonveyorEnvironment) {
        val record = context.records.first()
        logger.trace(
            "topic = {}, partition = {}, offset = {}, key = {}, value = {}",
            objs = arrayOf(
                record.topic,
                record.partition,
                record.offset,
                record.key,
                record.value
            )
        )
        try {
            context.metaInfo = context.jacksonSerializer.readValue(
                record.value,
                ConverterMeltInfo::class.java
            )
        } catch (e: Throwable) {
            val msg = e.message ?: ""
            logger.error(msg)
            context.errors.add(CorError(msg))
            context.status = CorStatus.FAILING
        }
    }

    override fun match(context: ConverterAngleContext<String, String>, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
