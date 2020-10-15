package ru.datana.smart.ui.converter.app.mappings

import org.apache.kafka.clients.consumer.ConsumerRecord
import ru.datana.smart.ui.converter.app.cor.context.InnerRecord
import ru.datana.smart.ui.converter.ws.models.WsDsmartConverters
import ru.datana.smart.ui.ml.models.TemperatureProcUiDto

fun <K, V> ConsumerRecord<K, V>.toInnerModel(): InnerRecord<K, V> = InnerRecord(
    topic = topic(),
    partition = partition(),
    offset = offset(),
    key = key(),
    value = value()
)

fun toWsConverterModel(temperatureProcUiDto: TemperatureProcUiDto) =
    WsDsmartConverters(
        tiltAngle = temperatureProcUiDto.temperatureAverage
    )
