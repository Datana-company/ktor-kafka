package ru.datana.smart.ui.converter.app.mappings

import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.events.IBizEvent
import ru.datana.smart.ui.converter.common.models.*
import ru.datana.smart.ui.converter.ws.models.*
import java.time.Instant
import kotlin.streams.toList

fun toWsConverterSlagRateModel(modelSlagRate: ModelSlagRate) =
    WsDsmartConverterSlagRate(
        steelRate = modelSlagRate.steelRate.takeIf { it != Double.MIN_VALUE },
        slagRate = modelSlagRate.slagRate.takeIf { it != Double.MIN_VALUE }
    )

fun toWsConverterAnglesModel(modelAngles: ModelAngles) =
    WsDsmartConverterAngles(
        angleTime = modelAngles.angleTime.takeIf { it != Instant.MIN }?.toEpochMilli(),
        angle = modelAngles.angle.takeIf { it != Double.MIN_VALUE },
        source = modelAngles.source.takeIf { it != Double.MIN_VALUE }
    )

fun toWsConverterFrameDataModel(modelFrame: ModelFrame) =
    WsDsmartConverterFrameData(
        frameId = modelFrame.frameId.takeIf { it.isNotBlank() },
        frameTime = modelFrame.frameTime.takeIf { it != Instant.MIN }?.toEpochMilli(),
        framePath = modelFrame.framePath.takeIf { it.isNotBlank() },
        image = modelFrame.image.takeIf { it.isNotBlank() },
        channel = modelFrame.channel.takeIf { it != ModelFrame.Channels.NONE }.toString()
    )

fun toWsConverterMeltInfoModel(modelMeltInfo: ModelMeltInfo) =
    WsDsmartConverterMeltInfo(
        id = modelMeltInfo.id.takeIf { it.isNotBlank() },
        timeStart = modelMeltInfo.timeStart.takeIf { it != Instant.MIN }?.toEpochMilli(),
        meltNumber = modelMeltInfo.meltNumber.takeIf { it.isNotBlank() },
        steelGrade = modelMeltInfo.steelGrade.takeIf { it.isNotBlank() },
        crewNumber = modelMeltInfo.crewNumber.takeIf { it.isNotBlank() },
        shiftNumber = modelMeltInfo.shiftNumber.takeIf { it.isNotBlank() },
        mode = modelMeltInfo.mode.takeIf { it != ModelMeltInfo.Mode.NONE }?.let { WsDsmartConverterMeltInfo.Mode.valueOf(it.name) },
        devices = WsDsmartConverterMeltDevices(
            converter = WsDsmartConverterDevicesConverter(
                id = modelMeltInfo.devices.converter.id.takeIf { it.isNotBlank() },
                name = modelMeltInfo.devices.converter.name.takeIf { it.isNotBlank() },
                uri = modelMeltInfo.devices.converter.uri.takeIf { it.isNotBlank() },
                deviceType = modelMeltInfo.devices.converter.deviceType.takeIf { it.isNotBlank() },
                type = modelMeltInfo.devices.converter.type.takeIf { it != ModelDeviceType.NONE }?.let { WsDsmartConverterDeviceType.valueOf(it.name) }
            ),
            irCamera = WsDsmartConverterDevicesIrCamera(
                id = modelMeltInfo.devices.converter.id.takeIf { it.isNotBlank() },
                name = modelMeltInfo.devices.converter.name.takeIf { it.isNotBlank() },
                uri = modelMeltInfo.devices.converter.uri.takeIf { it.isNotBlank() },
                deviceType = modelMeltInfo.devices.converter.deviceType.takeIf { it.isNotBlank() },
                type = modelMeltInfo.devices.irCamera.type.takeIf { it != ModelDeviceType.NONE }?.let { WsDsmartConverterDeviceType.valueOf(it.name) }
            ),
            selsyn = WsDsmartConverterDevicesSelsyn(
                id = modelMeltInfo.devices.converter.id.takeIf { it.isNotBlank() },
                name = modelMeltInfo.devices.converter.name.takeIf { it.isNotBlank() },
                uri = modelMeltInfo.devices.converter.uri.takeIf { it.isNotBlank() },
                deviceType = modelMeltInfo.devices.converter.deviceType.takeIf { it.isNotBlank() },
                type = modelMeltInfo.devices.selsyn.type.takeIf { it != ModelDeviceType.NONE }?.let { WsDsmartConverterDeviceType.valueOf(it.name) }
            ),
            slagRate = WsDsmartConverterDevicesSlagRate(
                id = modelMeltInfo.devices.converter.id.takeIf { it.isNotBlank() },
                name = modelMeltInfo.devices.converter.name.takeIf { it.isNotBlank() },
                uri = modelMeltInfo.devices.converter.uri.takeIf { it.isNotBlank() },
                deviceType = modelMeltInfo.devices.converter.deviceType.takeIf { it.isNotBlank() },
                type = modelMeltInfo.devices.slagRate.type.takeIf { it != ModelDeviceType.NONE }?.let { WsDsmartConverterDeviceType.valueOf(it.name) }
            )
        )
    )

fun toWsEventModel(event: IBizEvent) =
    WsDsmartEvent(
        id = event.id,
        timeStart = event.timeStart.toEpochMilli(),
        timeFinish = event.timeFinish.toEpochMilli(),
        title = event.title,
        textMessage = event.textMessage,
        category = WsDsmartEvent.Category.valueOf(event.category.name),
        isActive = event.isActive,
        executionStatus = WsDsmartEvent.ExecutionStatus.valueOf(event.executionStatus.name)
    )

fun toWsEventListModel(modelEvents: ModelEvents) = WsDsmartEventList(
    list = modelEvents.events.stream().map { event -> toWsEventModel(event) }
        ?.toList()
        ?: mutableListOf()
)

fun toWsConverterStateModel(context: ConverterBeContext) =
    WsDsmartConverterState(
        meltInfo = context.currentState.get()?.currentMeltInfo?.let { toWsConverterMeltInfoModel(it) },
        events = toWsEventListModel(context.events),
        warningPoint = context.metalRateWarningPoint
    )
