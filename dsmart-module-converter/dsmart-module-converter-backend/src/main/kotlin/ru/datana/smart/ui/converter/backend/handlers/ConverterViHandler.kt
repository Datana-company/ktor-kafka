package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorError
import ru.datana.smart.ui.converter.common.context.CorStatus

object ConverterViHandler : IKonveyorHandler<ConverterBeContext> {

    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
//        val record = context.records.firstOrNull { it.topic == context.topicVideo } ?: return
//
//        context.logger.trace("topic = {}, partition = {}, offset = {}, key = {}, value = {}",
//            objs = arrayOf(
//                record.topic,
//                record.partition,
//                record.offset,
//                record.key,
//                record.value
//            ))
//        val record = "{\"frameId\": \"2\", \"frameTime\": 1601796315751, \"framePath\": \"/frame/to/path\", \"meltInfo\": {\"id\": \"2\", \"timeStart\": 1601796302129, \"meltNumber\": \"11\", \"steelGrade\": \"ММК\", \"crewNumber\": \"1\", \"shiftNumber\": \"3\", \"mode\": 1, \"devices\": {\"irCamera\": {\"id\": \"c5542e80-50f0-4f1f-bf8e-66eb52452f68\", \"name\": \"GoPro\", \"uri\": \"video/path\", \"type\": 1}}}}"
//
//        try {
//            val obj = context.jacksonSerializer.readValue(record/*.value*/, ConverterTransportViMl::class.java)!!
//
//            val response = WsDsmartResponseConverterVi(
//                data = toWsConverterViModel(obj)
//            )
//
//            context.forwardObjects.add(response)
//
//        } catch (e: Throwable) {
//            val msg = "Error parsing data for [Proc]: ${record/*.value*/}"
//            context.logger.error(msg)
//            context.errors.add(CorError(msg))
//            context.status = CorStatus.FAILING
//        }
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }

}
