package ru.datana.smart.ui.converter.app.mappings

import ru.datana.smart.ui.converter.common.events.IBizEvent
import ru.datana.smart.ui.converter.common.models.*
import ru.datana.smart.ui.converter.ws.models.*
import kotlin.streams.toList

fun toWsConverterSlagRateModel(modelSlagRate: ModelSlagRate) =
    WsDsmartConverterSlagRate(
        steelRate = modelSlagRate.steelRate,
        slagRate = modelSlagRate.slagRate
    )

fun toWsConverterAnglesModel(modelAngles: ModelAngles) =
    WsDsmartConverterAngles(
        angle = modelAngles.angle
    )

fun toWsConverterFrameModel(modelFrame: ModelFrame) =
    WsDsmartConverterFrame(
        frameId = modelFrame.frameId,
        frameTime = modelFrame.frameTime,
        framePath = modelFrame.framePath
    )

fun toWsConverterMeltInfoModel(modelMeltInfo: ModelMeltInfo) =
    WsDsmartConverterMeltInfo(
        id = modelMeltInfo.id,
        timeStart = modelMeltInfo.timeStart,
        meltNumber = modelMeltInfo.meltNumber,
        steelGrade = modelMeltInfo.steelGrade,
        crewNumber = modelMeltInfo.crewNumber,
        shiftNumber = modelMeltInfo.shiftNumber,
        mode = WsDsmartConverterMeltInfo.Mode.valueOf(modelMeltInfo.mode.toString()),
        devices = WsDsmartConverterMeltDevices(
            irCamera = WsDsmartConverterDevicesIrCamera(
                id = modelMeltInfo.devices?.irCamera?.id,
                name = modelMeltInfo.devices?.irCamera?.name,
                uri = modelMeltInfo.devices?.irCamera?.uri,
                type = WsDsmartConverterDevicesIrCamera.Type.valueOf(modelMeltInfo.devices?.irCamera?.type.toString())
            )
        ),
    )

fun toWsEventModel(event: IBizEvent) =
    WsDsmartEvent(
        id = event.id,
        timeStart = event.timeStart,
        timeFinish = event.timeFinish,
        title = event.title,
        textMessage = event.textMessage,
        category = WsDsmartEvent.Category.valueOf(event.category.toString()),
        isActive = event.isActive
    )

fun toWsEventListModel(events: List<IBizEvent>) = events.stream().map { event -> toWsEventModel(event) }.toList()

fun toWsEventListModel(modelEvents: ModelEvents) = modelEvents.events?.stream()?.map { event -> toWsEventModel(event) }
    ?.toList()
    ?: mutableListOf()

fun toWsTemperatureModel(modelTemperature: ModelTemperature) =
    WsDsmartTemperature(
        temperatureAverage = modelTemperature.temperatureAverage
    )
