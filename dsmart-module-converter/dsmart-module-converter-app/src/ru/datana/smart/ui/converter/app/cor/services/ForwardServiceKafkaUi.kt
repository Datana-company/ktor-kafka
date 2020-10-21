package ru.datana.smart.ui.converter.app.cor.services

import codes.spectrum.konveyor.DefaultKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.konveyor
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.serialization.json.Json
import ru.datana.smart.logger.DatanaLogContext
import ru.datana.smart.ui.converter.app.common.RecommendationTimer
import ru.datana.smart.ui.converter.app.cor.context.ConverterBeContext
import ru.datana.smart.ui.converter.app.cor.handlers.*
import ru.datana.smart.ui.converter.app.websocket.WsManager

class ForwardServiceKafkaUi(
    var logger: DatanaLogContext,
    var jacksonSerializer: ObjectMapper,
    var kotlinxSerializer: Json,
    var wsManager: WsManager,
    var topicTemperature: String,
    var topicConverter: String,
    var topicVideo: String,
    var topicMeta: String,
    var recommendationTimer: RecommendationTimer,
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
                it.topicTemperature = topicTemperature
                it.topicConverter = topicConverter
                it.topicVideo = topicVideo
                it.topicMeta = topicMeta
                it.recommendationTimer = recommendationTimer
                it.sensorId = sensorId
            },
            env
        )
    }

    companion object {
        val konveyor = konveyor<ConverterBeContext<String, String>> {

            timeout { 1000 }

            +TemperatureTopicHandler
            +ConverterUiHandler
            +ConverterViHandler
            +ConverterMetaHandler
            +RecommendationHandler
            +JsonSerializerHandler
            +WsSendHandler
            +FinishHandler
        }
    }
}
