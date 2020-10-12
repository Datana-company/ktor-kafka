package ru.datana.smart.ui.temperature.app.cor.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.ml.models.TemperatureMlUiDto
import ru.datana.smart.ui.temperature.app.cor.context.CorStatus
import ru.datana.smart.ui.temperature.app.cor.context.TemperatureBeContext
import ru.datana.smart.ui.temperature.ws.models.WsDsmartResponseAnalysis
import ru.datana.smart.ui.temperature.app.mappings.toWsAnalysisModel
import kotlin.math.max

object AnalysisTopicHandler : IKonveyorHandler<TemperatureBeContext<String, String>> {

    override suspend fun exec(context: TemperatureBeContext<String, String>, env: IKonveyorEnvironment) {
        val topicAnalysis = env.get<String>("topicAnalysis", String::class)
        val sensorId = env.get<String>("sensorId", String::class)
        val record = context.records.firstOrNull { it.topic == topicAnalysis } ?: return

        context.logger.trace("topic = ${record.topic}, partition = ${record.partition}, offset = ${record.offset}, key = ${record.key}, value = ${record.value}")

        record.value.let { json ->
            try {
                val obj = context.jacksonMapper.readValue(json, TemperatureMlUiDto::class.java)!!
                if (obj.version != "0.2") {
                    context.logger.error("Wrong TemperatureUI (input ML-data) version ")
                    return
                }
                if (obj.sensorId?.trim() != sensorId) {
                    context.logger.trace("Sensor Id {} is not proper in respect to {}", objs = *arrayOf(obj.sensorId, sensorId))
                    return
                }

                context.logger.trace("Checking time {}", obj.timeActual)
                val objTime = obj.timeActual ?: return
                val newTime = context.lastTimeMl.updateAndGet {
                    max(objTime, it)
                }

                context.logger.trace("Test for actuality: {} === {}", objs = *arrayOf(objTime, newTime))
                // Пропускаем устаревшие данные
                if (newTime != objTime) return

                val response = WsDsmartResponseAnalysis(
                    data = toWsAnalysisModel(obj)
                )
                response.data?.timeActual?.apply { context.forwardObjects.add(response) }

            } catch (e: Throwable) {
                context.logger.error("Error parsing data for [ML]: {}", record.value)
            }
        }
    }

    override fun match(context: TemperatureBeContext<String, String>, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }

}
