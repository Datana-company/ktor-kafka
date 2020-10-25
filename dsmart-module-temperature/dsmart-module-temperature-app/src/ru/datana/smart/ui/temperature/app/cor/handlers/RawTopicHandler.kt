package ru.datana.smart.ui.temperature.app.cor.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.ml.models.TemperatureProcUiDto
import ru.datana.smart.ui.temperature.app.cor.context.CorError
import ru.datana.smart.ui.temperature.app.cor.context.CorStatus
import ru.datana.smart.ui.temperature.app.cor.context.TemperatureBeContext
import ru.datana.smart.ui.temperature.ws.models.WsDsmartResponseTemperature
import ru.datana.smart.ui.temperature.app.mappings.toWsTemperatureModel
import kotlin.math.max

object RawTopicHandler : IKonveyorHandler<TemperatureBeContext<String, String>> {

    override suspend fun exec(context: TemperatureBeContext<String, String>, env: IKonveyorEnvironment) {
        val record = context.records.firstOrNull { it.topic == context.topicRaw } ?: return

        context.logger.trace("topic = {}, partition = {}, offset = {}, key = {}, value = {}",
            objs = arrayOf(
                record.topic,
                record.partition,
                record.offset,
                record.key,
                record.value
            ))

        try {
            val obj = context.jacksonSerializer.readValue(record.value, TemperatureProcUiDto::class.java)!!
            if (obj.sensorId != context.sensorId) {
                val msg = "Sensor Id ${obj.sensorId} is not proper in respect to ${context.sensorId}"
                context.logger.trace(msg)
                context.errors.add(CorError(msg))
                context.status = CorStatus.FAILING
                return
            }

            val objTime = obj.timeIntervalLatest ?: return
            val newTime = context.lastTimeProc.updateAndGet {
                max(objTime, it)
            }

            // Пропускаем устаревшие данные
            if (newTime != objTime) return

            val response = WsDsmartResponseTemperature(
                data = toWsTemperatureModel(obj)
            )
            response.data?.temperatureAverage?.isFinite()?.apply { context.forwardObjects.add(response) }

        } catch (e: Throwable) {
            val msg = "Error parsing data for [Proc]: ${record.value}"
            context.logger.error(msg)
            context.errors.add(CorError(msg))
            context.status = CorStatus.FAILING
        }
    }

    override fun match(context: TemperatureBeContext<String, String>, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }

}
