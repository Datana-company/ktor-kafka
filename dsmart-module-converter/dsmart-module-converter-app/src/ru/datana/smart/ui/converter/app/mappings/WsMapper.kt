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
        mode = modelMeltInfo.mode?.let { WsDsmartConverterMeltInfo.Mode.valueOf(it.name) },
        devices = WsDsmartConverterMeltDevices(
            converter = WsDsmartConverterDevicesConverter(
                id = modelMeltInfo.devices?.converter?.id,
                name = modelMeltInfo.devices?.converter?.name,
                uri = modelMeltInfo.devices?.converter?.uri,
                deviceType = modelMeltInfo.devices?.converter?.deviceType,
                type = modelMeltInfo.devices?.converter?.type?.let { WsDsmartConverterDeviceType.valueOf(it.name) }
            ),
            irCamera = WsDsmartConverterDevicesIrCamera(
                id = modelMeltInfo.devices?.irCamera?.id,
                name = modelMeltInfo.devices?.irCamera?.name,
                uri = modelMeltInfo.devices?.irCamera?.uri,
                deviceType = modelMeltInfo.devices?.irCamera?.deviceType,
                type = modelMeltInfo.devices?.irCamera?.type?.let { WsDsmartConverterDeviceType.valueOf(it.name) }
            ),
            selsyn = WsDsmartConverterDevicesSelsyn(
                id = modelMeltInfo.devices?.selsyn?.id,
                name = modelMeltInfo.devices?.selsyn?.name,
                uri = modelMeltInfo.devices?.selsyn?.uri,
                deviceType = modelMeltInfo.devices?.selsyn?.deviceType,
                type = modelMeltInfo.devices?.selsyn?.type?.let { WsDsmartConverterDeviceType.valueOf(it.name) }
            ),
            slagRate = WsDsmartConverterDevicesSlagRate(
                id = modelMeltInfo.devices?.slagRate?.id,
                name = modelMeltInfo.devices?.slagRate?.name,
                uri = modelMeltInfo.devices?.slagRate?.uri,
                deviceType = modelMeltInfo.devices?.slagRate?.deviceType,
                type = modelMeltInfo.devices?.slagRate?.type?.let { WsDsmartConverterDeviceType.valueOf(it.name) }
            )
        )
    )

fun toWsEventModel(event: IBizEvent) =
    WsDsmartEvent(
        id = event.id,
        timeStart = event.timeStart,
        timeFinish = event.timeFinish,
        title = event.title,
        textMessage = event.textMessage,
        category = event.category?.let { WsDsmartEvent.Category.valueOf(it.name) },
        isActive = event.isActive,
        executionStatus = WsDsmartEvent.ExecutionStatus.valueOf(event.executionStatus.name)
    )

fun toWsEventListModel(events: List<IBizEvent>) = events.stream().map { event -> toWsEventModel(event) }.toList()

fun toWsEventListModel(modelEvents: ModelEvents) = modelEvents.events?.stream()?.map { event -> toWsEventModel(event) }
    ?.toList()
    ?: mutableListOf()

fun toWsTemperatureModel(modelTemperature: ModelTemperature) =
    WsDsmartTemperature(
        temperatureAverage = modelTemperature.temperatureAverage
    )
