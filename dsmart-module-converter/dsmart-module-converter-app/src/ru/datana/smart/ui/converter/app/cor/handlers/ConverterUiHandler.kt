package ru.datana.smart.ui.converter.app.cor.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.app.cor.context.ConverterBeContext
import ru.datana.smart.ui.converter.app.cor.context.CorError
import ru.datana.smart.ui.converter.app.cor.context.CorStatus
import ru.datana.smart.ui.converter.app.mappings.toWsConverterUiModel
import ru.datana.smart.ui.converter.ws.models.WsDsmartResponseConverterUi
import ru.datana.smart.ui.mlui.models.ConverterTransportMlUi

object ConverterUiHandler : IKonveyorHandler<ConverterBeContext<String, String>> {

    override suspend fun exec(context: ConverterBeContext<String, String>, env: IKonveyorEnvironment) {
//        val record = context.records.firstOrNull { it.topic == context.topicConverter } ?: return
//
//        context.logger.trace("topic = ${record.topic}, partition = ${record.partition}, offset = ${record.offset}, key = ${record.key}, value = ${record.value}")
        val record = "{\"frameId\": \"1\", \"frameTime\": 1602796315751, \"framePath\": \"/frame/to/path\", \"angle\": 79.123, \"steelRate\": 0.61, \"slagRate\": 0.05, \"meltInfo\": {\"id\": \"1\", \"timeStart\": 1602796302129, \"meltNumber\": \"12\", \"steelGrade\": \"ММК\", \"crewNumber\": \"3\", \"shiftNumber\": \"2\", \"mode\": 1, \"devices\": {\"irCamera\": {\"id\": \"c17ea7ca-7bbf-4f89-a644-7899f21ac629\", \"name\": \"GoPro\", \"uri\": \"video/path\", \"type\": 1}}}}"

        try {
            val obj = context.jacksonSerializer.readValue(record, ConverterTransportMlUi::class.java)!!

            val response = WsDsmartResponseConverterUi(
                data = toWsConverterUiModel(obj)
            )
            response.data?.angle?.isFinite()?.apply { context.forwardObjects.add(response) }

        } catch (e: Throwable) {
            val msg = "Error parsing data for [Proc]: ${record}"
            context.logger.error(msg)
            context.errors.add(CorError(msg))
            context.status = CorStatus.FAILING
        }
    }

    override fun match(context: ConverterBeContext<String, String>, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }

}
