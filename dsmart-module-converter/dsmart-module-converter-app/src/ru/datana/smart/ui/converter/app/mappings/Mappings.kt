package ru.datana.smart.ui.converter.app.mappings

import org.apache.kafka.clients.consumer.ConsumerRecord
import ru.datana.smart.ui.converter.app.cor.context.InnerRecord
import ru.datana.smart.ui.converter.app.cor.repository.events.IUserEvent
import ru.datana.smart.ui.converter.ws.models.*
import ru.datana.smart.ui.ml.models.TemperatureProcUiDto
import ru.datana.smart.ui.meta.models.ConverterMeltInfo
import ru.datana.smart.ui.mlui.models.ConverterTransportMlUi
import ru.datana.smart.ui.viml.models.ConverterTransportViMl
import kotlin.streams.toList

fun <K, V> ConsumerRecord<K, V>.toInnerModel(): InnerRecord<K, V> = InnerRecord(
    topic = topic(),
    partition = partition(),
    offset = offset(),
    key = key(),
    value = value()
)

fun toWsConverterUiModel(converterTransportMlUi: ConverterTransportMlUi) =
    WsDsmartConverterUi(
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

fun toWsConverterViModel(converterTransportMlVi: ConverterTransportViMl) =
    WsDsmartConverterVi(
        frameId = converterTransportMlVi.frameId,
        frameTime = converterTransportMlVi.frameTime,
        framePath = converterTransportMlVi.framePath,
        meltInfo = WsDsmartConverterMeltInfo(
            id = converterTransportMlVi.meltInfo?.id,
            timeStart = converterTransportMlVi.meltInfo?.timeStart,
            meltNumber = converterTransportMlVi.meltInfo?.meltNumber,
            steelGrade = converterTransportMlVi.meltInfo?.steelGrade,
            crewNumber = converterTransportMlVi.meltInfo?.crewNumber,
            shiftNumber = converterTransportMlVi.meltInfo?.shiftNumber,
            mode = WsDsmartConverterMeltInfo.Mode.valueOf(converterTransportMlVi.meltInfo?.mode.toString()),
            devices = WsDsmartConverterMeltDevices(
                irCamera = WsDsmartConverterDevicesIrCamerta(
                    id = converterTransportMlVi.meltInfo?.devices?.irCamera?.id,
                    name = converterTransportMlVi.meltInfo?.devices?.irCamera?.name,
                    uri = converterTransportMlVi.meltInfo?.devices?.irCamera?.uri,
                    type = WsDsmartConverterDevicesIrCamerta.Type.valueOf(converterTransportMlVi.meltInfo?.devices?.irCamera?.type.toString())
                )
            ),
        )
    )

fun toWsConverterMetaModel(converterMeltInfo: ConverterMeltInfo) =
    WsDsmartConverterMeltInfo(
        id = converterMeltInfo.id,
        timeStart = converterMeltInfo.timeStart,
        meltNumber = converterMeltInfo.meltNumber,
        steelGrade = converterMeltInfo.steelGrade,
        crewNumber = converterMeltInfo.crewNumber,
        shiftNumber = converterMeltInfo.shiftNumber,
        mode = WsDsmartConverterMeltInfo.Mode.valueOf(converterMeltInfo.mode.toString()),
        devices = WsDsmartConverterMeltDevices(
            irCamera = WsDsmartConverterDevicesIrCamerta(
                id = converterMeltInfo.devices?.irCamera?.id,
                name = converterMeltInfo.devices?.irCamera?.name,
                uri = converterMeltInfo.devices?.irCamera?.uri,
                type = WsDsmartConverterDevicesIrCamerta.Type.valueOf(converterMeltInfo.devices?.irCamera?.type.toString())
            )
        ),
    )

fun toWsEventModel(event: IUserEvent) =
    WsDsmartEvent(
        id = event.id,
        timeStart = event.timeStart,
        timeFinish = event.timeFinish,
        title = event.title,
        textMessage = event.textMessage,
        category = WsDsmartEvent.Category.valueOf(event.category.toString()),
        isActive = event.isActive
    )

fun toWsEventListModel(events: List<IUserEvent>) = events.stream().map { event -> toWsEventModel(event) }.toList()

fun toWsTemperatureModel(temperatureProcUiDto: TemperatureProcUiDto) =
    WsDsmartTemperature(
        temperatureAverage = temperatureProcUiDto.temperatureAverage
    )
