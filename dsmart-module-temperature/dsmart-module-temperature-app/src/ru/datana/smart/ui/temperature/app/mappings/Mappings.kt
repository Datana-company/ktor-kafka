package ru.datana.smart.ui.temperature.app.mappings

import org.apache.kafka.clients.consumer.ConsumerRecord
import ru.datana.smart.ui.ml.models.TemperatureMlUiDto
import ru.datana.smart.ui.ml.models.TemperatureProcUiDto
import ru.datana.smart.ui.ml.models.TemperatureProcMlDto
import ru.datana.smart.ui.temperature.app.cor.context.InnerRecord
import ru.datana.smart.ui.temperature.ws.models.TeapotState
import ru.datana.smart.ui.temperature.ws.models.WsDsmartAnalysis
import ru.datana.smart.ui.temperature.ws.models.WsDsmartTemperatures
import java.time.Instant

fun <K, V> ConsumerRecord<K, V>.toInnerModel(): InnerRecord<K, V> = InnerRecord(
    topic = topic(),
    partition = partition(),
    offset = offset(),
    key = key(),
    value = value()
)

fun toWsAnalysisModel(temperatureMlUiDto: TemperatureMlUiDto) =
    WsDsmartAnalysis(
        timeBackend = Instant.now().toEpochMilli(),
        timeActual = temperatureMlUiDto.timeActual,
        durationToBoil = temperatureMlUiDto.durationToBoil,
        sensorId = temperatureMlUiDto.sensorId,
        temperatureLast = temperatureMlUiDto.temperatureLast,
        state = temperatureMlUiDto.state?.toWs()
    )

fun toWsTemperatureModel(temperatureProcUiDto: TemperatureProcUiDto) =
    WsDsmartTemperatures(
        timeBackend = Instant.now().toEpochMilli(),
        timeLatest = temperatureProcUiDto.timeIntervalLatest,
        timeEarliest = temperatureProcUiDto.timeIntervalEarliest,
        temperatureScale = temperatureProcUiDto.temperatureScale?.value,
        temperatureAverage = temperatureProcUiDto.temperatureAverage,
        tempertureMax = temperatureProcUiDto.temperatureMax,
        tempertureMin = temperatureProcUiDto.temperatureMin
    )

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
