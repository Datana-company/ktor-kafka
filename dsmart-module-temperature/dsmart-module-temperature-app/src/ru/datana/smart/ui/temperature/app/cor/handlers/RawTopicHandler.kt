package ru.datana.smart.ui.temperature.app.cor.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import io.ktor.application.log
import ru.datana.smart.ui.ml.models.TemperatureProcUiDto
import ru.datana.smart.ui.temperature.app.cor.context.CorStatus
import ru.datana.smart.ui.temperature.app.cor.context.TemperatureBeContext
import ru.datana.smart.ui.temperature.ws.models.WsDsmartResponseTemperature
import ru.datana.smart.ui.temperature.app.mappings.toWsTemperatureModel
import kotlin.math.max

object RawTopicHandler : IKonveyorHandler<TemperatureBeContext<String, String>> {

    override suspend fun exec(context: TemperatureBeContext<String, String>, env: IKonveyorEnvironment) {
        val record = context.records.firstOrNull { it.topic == context.topicRaw } ?: return

        context.logger.trace("topic = ${record.topic}, partition = ${record.partition}, offset = ${record.offset}, key = ${record.key}, value = ${record.value}")

        try {
            val obj = context.jacksonMapper.readValue(record.value, TemperatureProcUiDto::class.java)!!
            if (obj.sensorId != context.sensorId) return

            val objTime = obj.timeIntervalLatest ?: return
            val newTime = context.lastTimeProc.updateAndGet {
                max(objTime, it)
            }

            // Пропускаем устаревшие данные
            if (newTime != objTime) return

            val response = WsDsmartResponseTemperature(
                data = toWsTemperatureModel(obj)
            )
            response.data?.temperatureAverage?.isFinite()?.apply { context.forwardObjects += response }

        } catch (e: Throwable) {
            context.logger.error("Error parsing data for [Proc]: {}", record.value)
        }
    }

    override fun match(context: TemperatureBeContext<String, String>, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }

}
