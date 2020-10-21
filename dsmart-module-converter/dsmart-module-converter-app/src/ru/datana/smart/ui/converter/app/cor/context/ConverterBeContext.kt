package ru.datana.smart.ui.converter.app.cor.context

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.serialization.json.Json
import ru.datana.smart.common.transport.models.ws.IWsDsmartResponse
import ru.datana.smart.logger.DatanaLogContext
import ru.datana.smart.ui.converter.app.common.RecommendationTimer
import ru.datana.smart.ui.converter.app.websocket.WsManager
import java.time.Instant
import java.util.concurrent.atomic.AtomicLong

class ConverterBeContext<K, V> (

    var records: Collection<InnerRecord<K, V>>,
    var lastTimeProc: AtomicLong = AtomicLong(0),
    var forwardObjects: MutableCollection<IWsDsmartResponse<*>> = mutableListOf(),
    var forwardJsonObjects: Collection<String> = mutableListOf(),
    var status: CorStatus = CorStatus.STARTED,
    var errors: MutableList<CorError> = mutableListOf(),
    var timeStart: Instant = Instant.now(),
    var timeStop: Instant = Instant.now()
) {

    lateinit var logger: DatanaLogContext
    lateinit var jacksonSerializer: ObjectMapper
    lateinit var kotlinxSerializer: Json
    lateinit var wsManager: WsManager
    lateinit var topicTemperature: String
    lateinit var topicConverter: String
    lateinit var topicVideo: String
    lateinit var topicMeta: String
    lateinit var recommendationTimer: RecommendationTimer
    lateinit var sensorId: String
}
