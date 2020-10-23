package ru.datana.smart.ui.converter.app.cor.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.app.cor.context.ConverterBeContext
import ru.datana.smart.ui.converter.app.cor.context.CorError
import ru.datana.smart.ui.converter.app.cor.context.CorStatus
import ru.datana.smart.ui.converter.app.mappings.toWsConverterViModel
import ru.datana.smart.ui.converter.ws.models.WsDsmartResponseConverterVi
import ru.datana.smart.ui.viml.models.ConverterTransportViMl

object ConverterViHandler : IKonveyorHandler<ConverterBeContext<String, String>> {

    override suspend fun exec(context: ConverterBeContext<String, String>, env: IKonveyorEnvironment) {
        val record = context.records.firstOrNull { it.topic == context.topicVideo } ?: return

        context.logger.trace("topic = ${record.topic}, partition = ${record.partition}, offset = ${record.offset}, key = ${record.key}, value = ${record.value}")
//        val record = "{\"frameId\": \"2\", \"frameTime\": 1601796315751, \"framePath\": \"/frame/to/path\", \"meltInfo\": {\"id\": \"2\", \"timeStart\": 1601796302129, \"meltNumber\": \"11\", \"steelGrade\": \"ММК\", \"crewNumber\": \"1\", \"shiftNumber\": \"3\", \"mode\": 1, \"devices\": {\"irCamera\": {\"id\": \"c5542e80-50f0-4f1f-bf8e-66eb52452f68\", \"name\": \"GoPro\", \"uri\": \"video/path\", \"type\": 1}}}}"

        try {
            val obj = context.jacksonSerializer.readValue(record.value, ConverterTransportViMl::class.java)!!

            val response = WsDsmartResponseConverterVi(
                data = toWsConverterViModel(obj)
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
