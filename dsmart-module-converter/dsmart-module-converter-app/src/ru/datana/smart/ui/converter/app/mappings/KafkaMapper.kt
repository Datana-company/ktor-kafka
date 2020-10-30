package ru.datana.smart.ui.converter.app.mappings

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.consumer.ConsumerRecord
import ru.datana.smart.ui.converter.common.context.InnerRecord
import ru.datana.smart.ui.meta.models.ConverterMeltInfo
import ru.datana.smart.ui.mlui.models.ConverterTransportMlUi
import ru.datana.smart.ui.viml.models.ConverterTransportViMl
import ru.datana.smart.ui.mlui.models.ConverterTransportAngle

fun <K, V> ConsumerRecord<K, V>.toInnerModel(): InnerRecord<K, V> = InnerRecord(
    topic = topic(),
    partition = partition(),
    offset = offset(),
    key = key(),
    value = value()
)

val jacksonSerializer: ObjectMapper = ObjectMapper()

fun toConverterMeltInfo(record: InnerRecord<String, String>): ConverterMeltInfo {
    return try {
        jacksonSerializer.readValue(record.value, ConverterMeltInfo::class.java)!!
    } catch (e: Exception) {
        // TODO: добавить обработку исключения
        ConverterMeltInfo()
    }
}

fun toConverterTransportMlUi(record: InnerRecord<String, String>): ConverterTransportMlUi {
    return try {
        jacksonSerializer.readValue(record.value, ConverterTransportMlUi::class.java)!!
    } catch (e: Exception) {
        // TODO: добавить обработку исключения
        ConverterTransportMlUi()
    }
}

fun toConverterTransportViMl(record: InnerRecord<String, String>): ConverterTransportViMl {
    return try {
        jacksonSerializer.readValue(record.value, ConverterTransportViMl::class.java)!!
    } catch (e: Exception) {
        // TODO: добавить обработку исключения
        ConverterTransportViMl()
    }
}

fun toConverterTransportAngle(record: InnerRecord<String, String>): ConverterTransportAngle {
    return try {
        jacksonSerializer.readValue(record.value, ConverterTransportAngle::class.java)!!
    } catch (e: Exception) {
        // TODO: добавить обработку исключения
        ConverterTransportAngle()
    }
}
