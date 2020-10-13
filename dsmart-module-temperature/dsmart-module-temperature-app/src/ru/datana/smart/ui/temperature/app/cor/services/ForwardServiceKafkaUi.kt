package ru.datana.smart.ui.temperature.app.cor.services

import codes.spectrum.konveyor.DefaultKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.konveyor
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.serialization.json.Json
import ru.datana.smart.logger.DatanaLogContext
import ru.datana.smart.ui.temperature.app.cor.context.TemperatureBeContext
import ru.datana.smart.ui.temperature.app.cor.handlers.*
import ru.datana.smart.ui.temperature.app.websocket.WsManager

class ForwardServiceKafkaUi(
    var logger: DatanaLogContext,
    var jacksonSerializer: ObjectMapper,
    var kotlinxSerializer: Json,
    var wsManager: WsManager,
    var topicRaw: String,
    var topicAnalysis: String,
    var sensorId: String,
) {

    suspend fun exec(context: TemperatureBeContext<String, String>) {
        exec(context, DefaultKonveyorEnvironment)
    }

    suspend fun exec(context: TemperatureBeContext<String, String>, env: IKonveyorEnvironment) {
        konveyor.exec(
            context.also {
                it.logger = logger
                it.jacksonSerializer = jacksonSerializer
                it.kotlinxSerializer = kotlinxSerializer
                it.wsManager = wsManager
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
            +JsonSerializerHandler
            +WsSendHandler
            +FinishHandler
        }
    }
}
