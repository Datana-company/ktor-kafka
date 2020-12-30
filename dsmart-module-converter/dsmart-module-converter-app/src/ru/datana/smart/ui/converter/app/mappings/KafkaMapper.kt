package ru.datana.smart.ui.converter.app.mappings

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.consumer.ConsumerRecord
import ru.datana.smart.converter.transport.meta.models.ConverterMeltInfo
import ru.datana.smart.converter.transport.mlui.models.ConverterTransportAngle
import ru.datana.smart.ui.converter.common.exceptions.ConverterDeserializationException
import ru.datana.smart.ui.converter.common.context.InnerRecord
import ru.datana.smart.ui.extevent.models.ConverterTransportExternalEvent

fun ConsumerRecord<String, ByteArray>.toInnerModel(): InnerRecord = InnerRecord(
    topic = topic(),
    partition = partition(),
    offset = offset(),
    key = key(),
    value = value()
)

val jacksonSerializer: ObjectMapper = ObjectMapper()
    .configure(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS, true).configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false)
    // Если в десериализуемом JSON-е встретится поле, которого нет в классе,
    // то не будет выброшено исключение UnrecognizedPropertyException,
    // т.е. мы отменяем проверку строгого соответствия JSON и класса
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

fun toConverterMeltInfo(record: InnerRecord): ConverterMeltInfo {
    try {
        return jacksonSerializer.readValue(record.value.toString(), ConverterMeltInfo::class.java)!!
    } catch (e: Exception) {
        throw ConverterDeserializationException(e.message, e.cause)
    }
}

//fun toConverterTransportMlUi(record: InnerRecord<String, String>): ConverterTransportMlUi {
//    try {
//        return jacksonSerializer.readValue(record.value, ConverterTransportMlUi::class.java)!!
//    } catch (e: Exception) {
//        throw ConverterDeserializationException(e.message, e.cause)
//    }
//}

//fun toConverterTransportViMl(record: InnerRecord<String, String>): ConverterTransportViMl {
//    try {
//        return jacksonSerializer.readValue(record.value, ConverterTransportViMl::class.java)!!
//    } catch (e: Exception) {
//        throw ConverterDeserializationException(e.message, e.cause)
//    }
//}

fun toConverterTransportAngle(record: InnerRecord): ConverterTransportAngle {
    try {
        return jacksonSerializer.readValue(record.value, ConverterTransportAngle::class.java)!!
    } catch (e: Exception) {
        throw ConverterDeserializationException(e.message, e.cause)
    }
}

fun toConverterTransportExternalEvents(record: InnerRecord): ConverterTransportExternalEvent {
    try {
        return jacksonSerializer.readValue(record.value, ConverterTransportExternalEvent::class.java)!!
    } catch (e: Exception) {
        throw ConverterDeserializationException(e.message, e.cause)
    }
}
