package ru.datana.smart.ui.temperature.app.cor.services

import codes.spectrum.konveyor.DefaultKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.konveyor
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.serialization.json.Json
import ru.datana.smart.logger.DatanaLogContext
import ru.datana.smart.ui.temperature.app.cor.context.TemperatureBeContext
import ru.datana.smart.ui.temperature.app.cor.handlers.AnalysisTopicHandler
import ru.datana.smart.ui.temperature.app.cor.handlers.RawTopicHandler

class ForwardServiceKafkaUi(
    var logger: DatanaLogContext,
    var jacksonSerializer: ObjectMapper,
    var kotlinxSerializer: Json,
    var topicRaw: String,
    var topicAnalysis: String,
    var sensorId: String,
) {

    suspend fun exec(context: TemperatureBeContext<String, String>) {
        konveyor.exec(context, DefaultKonveyorEnvironment)
    }

    suspend fun exec(context: TemperatureBeContext<String, String>, env: IKonveyorEnvironment) {
        konveyor.exec(
            context.also {
                it.logger = logger
                it.jacksonSerializer = jacksonSerializer
                it.kotlinxSerializer = kotlinxSerializer
                it.topicRaw = topicRaw
                it.topicAnalysis = topicAnalysis
                it.sensorId = sensorId
            },
            env
        )
    }

    companion object {
        val konveyor = konveyor<TemperatureBeContext<String, String>> {

            timeout { 1000 }

            +RawTopicHandler
            +AnalysisTopicHandler
        }
    }
}
