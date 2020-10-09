package ru.datana.smart.ui.temperature.app.mappings

import org.apache.kafka.clients.consumer.ConsumerRecord
import ru.datana.smart.ui.ml.models.TemperatureMlUiDto
import ru.datana.smart.ui.temperature.ws.models.TeapotState
import ru.datana.smart.ui.temperature.app.cor.context.Record

fun <K, V> ConsumerRecord<K, V>.toInnerModel(): Record<K, V> = Record(
    topic = topic(),
    partition = partition(),
    offset = offset(),
    key = key(),
    value = value()
)

fun toWsModel() {
}

fun TemperatureMlUiDto.State.toWs() = when (this) {
    TemperatureMlUiDto.State.SWITCHED_ON -> TeapotState(
        id = "switchedOn",
        name = "Включен",
        message = "Чайник включен"
    )
    TemperatureMlUiDto.State.SWITCHED_OFF -> TeapotState(
        id = "switchedOff",
        name = "Выключен",
        message = "Чайник выключен"
    )
    else -> TeapotState(
        id = "unknown",
        name = "Неизвестно",
        message = "Состояние чайника неизвестно"
    )
}

