package ru.datana.smart.ui.converter.app.mappings

import ru.datana.smart.ui.converter.common.models.*
import ru.datana.smart.ui.meta.models.ConverterMeltInfo
import ru.datana.smart.ui.mlui.models.ConverterTransportMlUi
import ru.datana.smart.ui.viml.models.ConverterTransportViMl
import ru.datana.smart.ui.ml.models.TemperatureProcUiDto

fun toModelMeltInfo(converterMeltInfo: ConverterMeltInfo) =
    ModelMeltInfo(
        id = converterMeltInfo.id,
        timeStart = converterMeltInfo.timeStart,
        meltNumber = converterMeltInfo.meltNumber,
        steelGrade = converterMeltInfo.steelGrade,
        crewNumber = converterMeltInfo.crewNumber,
        shiftNumber = converterMeltInfo.shiftNumber,
        mode = ModelMeltInfo.Mode.valueOf(converterMeltInfo.mode.toString()),
        devices = ModelMeltDevices(
            irCamera = ModelDevicesIrCamera(
                id = converterMeltInfo.devices?.irCamera?.id,
                name = converterMeltInfo.devices?.irCamera?.name,
                uri = converterMeltInfo.devices?.irCamera?.uri,
                type = ModelDevicesIrCamera.Type.valueOf(converterMeltInfo.devices?.irCamera?.type.toString())
            )
        )
    )

fun toModelMeltInfo(converterTransportViMl: ConverterTransportViMl) =
    ModelMeltInfo(
        id = converterTransportViMl.meltInfo?.id,
        timeStart = converterTransportViMl.meltInfo?.timeStart,
        meltNumber = converterTransportViMl.meltInfo?.meltNumber,
        steelGrade = converterTransportViMl.meltInfo?.steelGrade,
        crewNumber = converterTransportViMl.meltInfo?.crewNumber,
        shiftNumber = converterTransportViMl.meltInfo?.shiftNumber,
        mode = ModelMeltInfo.Mode.valueOf(converterTransportViMl.meltInfo?.mode.toString()),
        devices = ModelMeltDevices(
            irCamera = ModelDevicesIrCamera(
                id = converterTransportViMl.meltInfo?.devices?.irCamera?.id,
                name = converterTransportViMl.meltInfo?.devices?.irCamera?.name,
                uri = converterTransportViMl.meltInfo?.devices?.irCamera?.uri,
                type = ModelDevicesIrCamera.Type.valueOf(converterTransportViMl.meltInfo?.devices?.irCamera?.type.toString())
            )
        )
    )

fun toModelMeltInfo(converterTransportMlUi: ConverterTransportMlUi) =
    ModelMeltInfo(
        id = converterTransportMlUi.meltInfo?.id,
        timeStart = converterTransportMlUi.meltInfo?.timeStart,
        meltNumber = converterTransportMlUi.meltInfo?.meltNumber,
        steelGrade = converterTransportMlUi.meltInfo?.steelGrade,
        crewNumber = converterTransportMlUi.meltInfo?.crewNumber,
        shiftNumber = converterTransportMlUi.meltInfo?.shiftNumber,
        mode = ModelMeltInfo.Mode.valueOf(converterTransportMlUi.meltInfo?.mode.toString()),
        devices = ModelMeltDevices(
            irCamera = ModelDevicesIrCamera(
                id = converterTransportMlUi.meltInfo?.devices?.irCamera?.id,
                name = converterTransportMlUi.meltInfo?.devices?.irCamera?.name,
                uri = converterTransportMlUi.meltInfo?.devices?.irCamera?.uri,
                type = ModelDevicesIrCamera.Type.valueOf(converterTransportMlUi.meltInfo?.devices?.irCamera?.type.toString())
            )
        )
    )

fun toModelFrame(converterTransportViMl: ConverterTransportViMl) =
    ModelFrame(
        frameId = converterTransportViMl.frameId,
        frameTime = converterTransportViMl.frameTime,
        framePath = converterTransportViMl.framePath
    )

fun toModelFrame(converterTransportMlUi: ConverterTransportMlUi) =
    ModelFrame(
        frameId = converterTransportMlUi.frameId,
        frameTime = converterTransportMlUi.frameTime,
        framePath = converterTransportMlUi.framePath
    )

fun toModelSlagRate(converterTransportMlUi: ConverterTransportMlUi) =
    ModelSlagRate(
        steelRate = converterTransportMlUi.steelRate,
        slagRate = converterTransportMlUi.slagRate
    )

fun toModelAngles(temperatureProcUiDto: TemperatureProcUiDto) =
    ModelAngles(
        angle = temperatureProcUiDto.temperatureAverage
    )

fun toModelTemperature(temperatureProcUiDto: TemperatureProcUiDto) =
    ModelTemperature(
        temperatureAverage = temperatureProcUiDto.temperatureAverage
    )
