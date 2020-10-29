package ru.datana.smart.ui.converter.angle.app.cor.context

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.serialization.json.Json
import org.apache.kafka.clients.producer.KafkaProducer
import ru.datana.smart.logger.DatanaLogContext
import java.time.Instant

class ConverterAngleContext<K, V>(
    var records: Collection<InnerRecord<K, V>>,
    var status: CorStatus = CorStatus.STARTED,
    var errors: MutableList<CorError> = mutableListOf(),
    var timeStart: Instant = Instant.now(),
    var timeStop: Instant = Instant.now(),
    var jacksonSerializer: ObjectMapper = ObjectMapper(),
    var kotlinxSerializer: Json = Json { encodeDefaults = true }
) {
    lateinit var logger: DatanaLogContext
    lateinit var kafkaProducer: KafkaProducer<String, String>
}
