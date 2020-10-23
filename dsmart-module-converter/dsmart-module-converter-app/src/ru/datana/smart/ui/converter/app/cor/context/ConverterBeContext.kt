package ru.datana.smart.ui.converter.app.cor.context

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.serialization.json.Json
import ru.datana.smart.common.transport.models.ws.IWsDsmartResponse
import ru.datana.smart.logger.DatanaLogContext
import ru.datana.smart.ui.converter.app.common.EventTimer
import ru.datana.smart.ui.converter.app.cor.repository.UserEventsRepository
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
    var timeStop: Instant = Instant.now(),
    var jacksonSerializer: ObjectMapper = ObjectMapper(),
    var kotlinxSerializer: Json = Json { encodeDefaults = true },
    var wsManager: WsManager = WsManager(),
    var topicTemperature: String = "",
    var topicConverter: String = "",
    var topicVideo: String = "",
    var topicMeta: String = "",
    var eventTimer: EventTimer = EventTimer(),
    var sensorId: String = "",
    var eventsRepository: UserEventsRepository = UserEventsRepository()
) {
    lateinit var logger: DatanaLogContext
}
