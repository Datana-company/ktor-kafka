package ru.datana.smart.ui.converter.app.cor.services

import codes.spectrum.konveyor.DefaultKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.konveyor
import ru.datana.smart.logger.DatanaLogContext
import ru.datana.smart.ui.converter.app.common.MetalRateEventGenerator
import ru.datana.smart.ui.converter.app.cor.context.ConverterBeContext
import ru.datana.smart.ui.converter.app.cor.handlers.*
import ru.datana.smart.ui.converter.app.cor.repository.UserEventsRepository
import ru.datana.smart.ui.converter.app.websocket.WsManager

class ForwardServiceKafkaUi(
    var logger: DatanaLogContext,
    var wsManager: WsManager,
    var topicTemperature: String,
    var topicConverter: String,
    var topicVideo: String,
    var topicMeta: String,
    var metalRateEventGenerator: MetalRateEventGenerator,
    var sensorId: String,
    var eventsRepository: UserEventsRepository
) {

    suspend fun exec(context: ConverterBeContext<String, String>) {
        exec(context, DefaultKonveyorEnvironment)
    }

    suspend fun exec(context: ConverterBeContext<String, String>, env: IKonveyorEnvironment) {
        konveyor.exec(
            context.also {
                it.logger = logger
                it.wsManager = wsManager
                it.topicTemperature = topicTemperature
                it.topicConverter = topicConverter
                it.topicVideo = topicVideo
                it.topicMeta = topicMeta
                it.metalRateEventGenerator = metalRateEventGenerator
                it.sensorId = sensorId
                it.eventsRepository = eventsRepository
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
            +EventHandler
            +JsonSerializerHandler
            +WsSendHandler
            +FinishHandler
        }
    }
}
