package ru.datana.smart.ui.converter.app.mappings

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.consumer.ConsumerRecord
import ru.datana.smart.ui.converter.common.context.InnerRecord
import ru.datana.smart.ui.meta.models.ConverterMeltInfo
import ru.datana.smart.ui.ml.models.TemperatureProcUiDto
import ru.datana.smart.ui.mlui.models.ConverterTransportMlUi
import ru.datana.smart.ui.viml.models.ConverterTransportViMl

fun <K, V> ConsumerRecord<K, V>.toInnerModel(): InnerRecord<K, V> = InnerRecord(
    topic = topic(),
    partition = partition(),
    offset = offset(),
    key = key(),
    value = value()
)

fun toConverterMeltInfo(record: InnerRecord<String, String>): ConverterMeltInfo {
    return try {
        ObjectMapper().readValue(record.value, ConverterMeltInfo::class.java)!!
    } catch (e: Exception) {
        ConverterMeltInfo()
    }
}

fun toConverterTransportMlUi(record: InnerRecord<String, String>): ConverterTransportMlUi {
    return try {
        ObjectMapper().readValue(record.value, ConverterTransportMlUi::class.java)!!
    } catch (e: Exception) {
        ConverterTransportMlUi()
    }
}

fun toConverterTransportViMl(record: InnerRecord<String, String>): ConverterTransportViMl {
    return try {
        ObjectMapper().readValue(record.value, ConverterTransportViMl::class.java)!!
    } catch (e: Exception) {
        ConverterTransportViMl()
    }
}

fun toTemperatureProcUiDto(record: InnerRecord<String, String>): TemperatureProcUiDto {
    return try {
        ObjectMapper().readValue(record.value, TemperatureProcUiDto::class.java)!!
    } catch (e: Exception) {
        TemperatureProcUiDto()
    }
}
