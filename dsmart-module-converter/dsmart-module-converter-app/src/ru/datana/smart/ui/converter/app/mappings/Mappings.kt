package ru.datana.smart.ui.converter.app.mappings

import org.apache.kafka.clients.consumer.ConsumerRecord
import ru.datana.smart.ui.converter.app.cor.context.InnerRecord
import ru.datana.smart.ui.converter.ws.models.*
import ru.datana.smart.ui.ml.models.TemperatureProcUiDto
import ru.datana.smart.ui.mlui.models.ConverterTransportMlUi

fun <K, V> ConsumerRecord<K, V>.toInnerModel(): InnerRecord<K, V> = InnerRecord(
    topic = topic(),
    partition = partition(),
    offset = offset(),
    key = key(),
    value = value()
)

fun toWsConverterModel(converterTransportMlUi: ConverterTransportMlUi) =
    WsDsmartConverter(
        frameId = converterTransportMlUi.frameId,
        frameTime = converterTransportMlUi.frameTime,
        framePath = converterTransportMlUi.framePath,
        meltInfo = WsDsmartConverterMeltInfo(
            id = converterTransportMlUi.meltInfo?.id,
            timeStart = converterTransportMlUi.meltInfo?.timeStart,
            meltNumber = converterTransportMlUi.meltInfo?.meltNumber,
            steelGrade = converterTransportMlUi.meltInfo?.steelGrade,
            crewNumber = converterTransportMlUi.meltInfo?.crewNumber,
            shiftNumber = converterTransportMlUi.meltInfo?.shiftNumber,
            mode = WsDsmartConverterMeltInfo.Mode.valueOf(converterTransportMlUi.meltInfo?.mode.toString()),
            devices = WsDsmartConverterMeltDevices(
                irCamera = WsDsmartConverterDevicesIrCamerta(
                    id = converterTransportMlUi.meltInfo?.devices?.irCamera?.id,
                    name = converterTransportMlUi.meltInfo?.devices?.irCamera?.name,
                    uri = converterTransportMlUi.meltInfo?.devices?.irCamera?.uri,
                    type = WsDsmartConverterDevicesIrCamerta.Type.valueOf(converterTransportMlUi.meltInfo?.devices?.irCamera?.type.toString())
                )
            ),
        ),
        angle  = converterTransportMlUi.angle,
        steelRate = converterTransportMlUi.steelRate,
        slagRate = converterTransportMlUi.slagRate
    )

fun toWsTemperatureModel(temperatureProcUiDto: TemperatureProcUiDto) =
    WsDsmartTemperature(
        temperatureAverage = temperatureProcUiDto.temperatureAverage
    )
