package ru.datana.smart.ui.converter.app.cor.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.app.cor.context.ConverterBeContext
import ru.datana.smart.ui.converter.app.cor.context.CorError
import ru.datana.smart.ui.converter.app.cor.context.CorStatus
import ru.datana.smart.ui.converter.app.mappings.toWsConverterMetaModel
import ru.datana.smart.ui.converter.ws.models.WsDsmartResponseConverterMeta
import ru.datana.smart.ui.meta.models.ConverterMeltInfo

object ConverterMetaHandler : IKonveyorHandler<ConverterBeContext<String, String>> {

    override suspend fun exec(context: ConverterBeContext<String, String>, env: IKonveyorEnvironment) {
        val record = context.records.firstOrNull { it.topic == context.topicMeta } ?: return

        context.logger.trace("topic = {}, partition = {}, offset = {}, key = {}, value = {}",
            objs = arrayOf(
                record.topic,
                record.partition,
                record.offset,
                record.key,
                record.value
        ))
//        val record = "{\"id\": \"3\", \"timeStart\": 1600796302129, \"meltNumber\": \"10\", \"steelGrade\": \"ММК\", \"crewNumber\": \"4\", \"shiftNumber\": \"1\", \"mode\": 1, \"devices\": {\"irCamera\": {\"id\": \"4da50297-9483-43c3-a619-f32e4c7084f4\", \"name\": \"GoPro\", \"uri\": \"video/path\", \"type\": 1}}}"

        try {
            val obj = context.jacksonSerializer.readValue(record.value, ConverterMeltInfo::class.java)!!

            val response = WsDsmartResponseConverterMeta(
                data = toWsConverterMetaModel(obj)
            )
            context.forwardObjects.add(response)

        } catch (e: Throwable) {
            val msg = "Error parsing data for [Proc]: ${record.value}"
            context.logger.error(msg)
            context.errors.add(CorError(msg))
            context.status = CorStatus.FAILING
        }
    }

    override fun match(context: ConverterBeContext<String, String>, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }

}
