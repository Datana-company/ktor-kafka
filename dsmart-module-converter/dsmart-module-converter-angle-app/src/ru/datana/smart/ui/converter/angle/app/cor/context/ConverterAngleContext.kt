//package ru.datana.smart.ui.converter.angle.app.cor.context
//
//import com.fasterxml.jackson.core.JsonParser
//import com.fasterxml.jackson.databind.ObjectMapper
//import kotlinx.serialization.json.Json
//import org.apache.kafka.clients.producer.KafkaProducer
//import ru.datana.smart.logger.DatanaLogContext
//import ru.datana.smart.ui.converter.angle.app.models.AngleSchedule
//import ru.datana.smart.ui.mlui.models.ConverterMeltInfo
//import ru.datana.smart.ui.mlui.models.ConverterTransportAngle
//import java.time.Instant
//
//class ConverterAngleContext(
//    var record: InnerRecord<String, String>,
//    var scheduleRelativePath: String? = null,
//    var metaInfo: ConverterMeltInfo? = null,
//    var angleSchedule: AngleSchedule? = null,
//    var status: CorStatus = CorStatus.STARTED,
//    var errors: MutableList<CorError> = mutableListOf(),
//    var timeStart: Instant = Instant.now(),
//    var timeStop: Instant = Instant.now(),
//    var jacksonSerializer: ObjectMapper = ObjectMapper().configure(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS, true),
//    var scheduleBasePath: String = "",
//    var topicAngles: String = ""
//) {
//    lateinit var kafkaProducer: KafkaProducer<String, String>
//}
