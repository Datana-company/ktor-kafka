package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorError
import ru.datana.smart.ui.converter.common.context.CorStatus
import kotlin.math.max

object TemperatureTopicHandler : IKonveyorHandler<ConverterBeContext> {

    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {

//        context.logger.trace("topic = {}, partition = {}, offset = {}, key = {}, value = {}",
//            objs = arrayOf(
//                record.topic,
//                record.partition,
//                record.offset,
//                record.key,
//                record.value
//            ))
//
//        try {
//            val obj = context.jacksonSerializer.readValue(record.value, TemperatureProcUiDto::class.java)!!
//            if (obj.sensorId != context.sensorId) {
//                val msg = "Sensor Id ${obj.sensorId} is not proper in respect to ${context.sensorId}"
//                context.logger.trace(msg)
//                context.errors.add(CorError(msg))
//                context.status = CorStatus.FAILING
//                return
//            }
//
//            val objTime = obj.timeIntervalLatest ?: return
//            val newTime = context.lastTimeProc.updateAndGet {
//                max(objTime, it)
//            }
//
//            // Пропускаем устаревшие данные
//            if (newTime != objTime) return
//
//            val response = WsDsmartResponseTemperature(
//                data = toWsTemperatureModel(obj)
//            )
//            response.data?.temperatureAverage?.isFinite()?.apply { context.forwardObjects.add(response) }
//
//        } catch (e: Throwable) {
//            val msg = "Error parsing data for [Proc]: ${record.value}"
//            context.logger.error(msg)
//            context.errors.add(CorError(msg))
//            context.status = CorStatus.FAILING
//        }
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }

}
