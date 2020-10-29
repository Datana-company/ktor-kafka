package ru.datana.smart.ui.converter.angle.app.cor.context

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.serialization.json.Json
import org.apache.kafka.clients.producer.KafkaProducer
import ru.datana.smart.logger.DatanaLogContext
import ru.datana.smart.ui.converter.angle.app.models.AngleSchedule
import ru.datana.smart.ui.mlui.models.ConverterMeltInfo
import ru.datana.smart.ui.mlui.models.ConverterTransportAngle
import java.time.Instant

class ConverterAngleContext<K, V>(
    var records: Collection<InnerRecord<K, V>>,
    var forwardObjects: MutableList<ConverterTransportAngle> = mutableListOf(),
    var scheduleRelativePath: String? = null,
    var metaInfo: ConverterMeltInfo? = null,
    var angleSchedule: AngleSchedule? = null,
    var status: CorStatus = CorStatus.STARTED,
    var errors: MutableList<CorError> = mutableListOf(),
    var timeStart: Instant = Instant.now(),
    var timeStop: Instant = Instant.now(),
    var jacksonSerializer: ObjectMapper = ObjectMapper(),
) {
    lateinit var logger: DatanaLogContext
    lateinit var scheduleBasePath: String
    lateinit var kafkaProducer: KafkaProducer<String, String>
    lateinit var topicAngles: String
}
