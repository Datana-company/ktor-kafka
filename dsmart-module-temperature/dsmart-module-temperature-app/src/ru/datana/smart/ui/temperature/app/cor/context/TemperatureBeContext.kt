package ru.datana.smart.ui.temperature.app.cor.context

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.serialization.json.Json
import ru.datana.smart.common.transport.models.ws.IWsDsmartResponse
import ru.datana.smart.logger.DatanaLogContext
import java.time.Instant
import java.util.concurrent.atomic.AtomicLong

class TemperatureBeContext<K, V> (

    var records: Collection<InnerRecord<K, V>>,
    var lastTimeMl: AtomicLong = AtomicLong(0),
    var lastTimeProc: AtomicLong = AtomicLong(0),
    var forwardObjects: MutableCollection<IWsDsmartResponse<*>> = mutableListOf(),
    var status: CorStatus = CorStatus.STARTED,
    var errors: MutableList<CorError> = mutableListOf(),
    var timeStart: Instant = Instant.now(),
    var timeStop: Instant = Instant.now()
) {

    lateinit var logger: DatanaLogContext
    lateinit var jacksonSerializer: ObjectMapper
    lateinit var kotlinxSerializer: Json
    lateinit var topicRaw: String
    lateinit var topicAnalysis: String
    lateinit var sensorId: String
}

