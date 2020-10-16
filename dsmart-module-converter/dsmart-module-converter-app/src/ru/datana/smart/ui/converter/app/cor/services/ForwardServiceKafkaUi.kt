package ru.datana.smart.ui.converter.app.cor.services

import codes.spectrum.konveyor.DefaultKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.konveyor
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.serialization.json.Json
import ru.datana.smart.logger.DatanaLogContext
import ru.datana.smart.ui.converter.app.cor.context.ConverterBeContext
import ru.datana.smart.ui.converter.app.cor.handlers.*
import ru.datana.smart.ui.converter.app.websocket.WsManager

class ForwardServiceKafkaUi(
    var logger: DatanaLogContext,
    var jacksonSerializer: ObjectMapper,
    var kotlinxSerializer: Json,
    var wsManager: WsManager,
    var topicRaw: String,
    var sensorId: String,
) {

    suspend fun exec(context: ConverterBeContext<String, String>) {
        exec(context, DefaultKonveyorEnvironment)
    }

    suspend fun exec(context: ConverterBeContext<String, String>, env: IKonveyorEnvironment) {
        konveyor.exec(
            context.also {
                it.logger = logger
                it.jacksonSerializer = jacksonSerializer
                it.kotlinxSerializer = kotlinxSerializer
                it.wsManager = wsManager
                it.topicRaw = topicRaw
                it.sensorId = sensorId
            },
            env
        )
    }

    companion object {
        val konveyor = konveyor<ConverterBeContext<String, String>> {

            timeout { 1000 }

            +RawTopicHandler
            +ConverterUiHandler
            +RecommendationHandler
            +JsonSerializerHandler
            +WsSendHandler
            +FinishHandler
        }
    }
}
