package ru.datana.smart.ui.converter.app.mappings

import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.models.*
import ru.datana.smart.ui.converter.ws.models.*
import java.time.Instant

fun ConverterBeContext.toWsConverterResponseSlagRate() =
    WsDsmartResponseConverterSlagRate(
        data = toWsConverterSlagRateModel(this.slagRate)
    )

fun ConverterBeContext.toWsConverterResponseAngles() =
    WsDsmartResponseConverterAngles(
        data = toWsConverterAnglesModel(this.angles)
    )

fun ConverterBeContext.toWsConverterResponseFrame() =
    WsDsmartResponseConverterFrame(
        data = toWsConverterFrameDataModel(this.frame)
    )

fun ConverterBeContext.toWsConverterResponseMeltInfo() =
    WsDsmartResponseConverterMeltInfo(
        data = toWsConverterMeltInfoModel(this.meltInfo)
    )

fun ConverterBeContext.toWsResponseConverterEvent() =
    WsDsmartResponseConverterEvents(
        data = toWsEventListModel(this.events)
    )

fun ConverterBeContext.toWsResponseConverterState() =
    WsDsmartResponseConverterState(
        data = toWsConverterStateModel(this)
    )

private fun toWsConverterSlagRateModel(modelSlagRate: ModelSlagRate) =
    WsDsmartConverterSlagRate(
        steelRate = modelSlagRate.steelRate.takeIf { it != Double.MIN_VALUE },
        slagRate = modelSlagRate.slagRate.takeIf { it != Double.MIN_VALUE }
    )

private fun toWsConverterAnglesModel(modelAngles: ModelAngles) =
    WsDsmartConverterAngles(
        angleTime = modelAngles.angleTime.takeIf { it != Instant.MIN }?.toEpochMilli(),
        angle = modelAngles.angle.takeIf { it != Double.MIN_VALUE },
        source = modelAngles.source.takeIf { it != Double.MIN_VALUE }
    )

private fun toWsConverterFrameDataModel(modelFrame: ModelFrame) =
    WsDsmartConverterFrameData(
        frameId = modelFrame.frameId.takeIf { it.isNotBlank() },
        frameTime = modelFrame.frameTime.takeIf { it != Instant.MIN }?.toEpochMilli(),
        framePath = modelFrame.framePath.takeIf { it.isNotBlank() },
        image = modelFrame.image.takeIf { it.isNotBlank() },
        channel = modelFrame.channel.takeIf { it != ModelFrame.Channels.NONE }.toString()
    )

private fun toWsConverterMeltInfoModel(modelMeltInfo: ModelMeltInfo) =
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
                id = modelMeltInfo.devices.irCamera.id.takeIf { it.isNotBlank() },
                name = modelMeltInfo.devices.irCamera.name.takeIf { it.isNotBlank() },
                uri = modelMeltInfo.devices.irCamera.uri.takeIf { it.isNotBlank() },
                deviceType = modelMeltInfo.devices.irCamera.deviceType.takeIf { it.isNotBlank() },
                type = modelMeltInfo.devices.irCamera.type.takeIf { it != ModelDeviceType.NONE }?.let { WsDsmartConverterDeviceType.valueOf(it.name) }
            ),
            selsyn = WsDsmartConverterDevicesSelsyn(
                id = modelMeltInfo.devices.selsyn.id.takeIf { it.isNotBlank() },
                name = modelMeltInfo.devices.selsyn.name.takeIf { it.isNotBlank() },
                uri = modelMeltInfo.devices.selsyn.uri.takeIf { it.isNotBlank() },
                deviceType = modelMeltInfo.devices.selsyn.deviceType.takeIf { it.isNotBlank() },
                type = modelMeltInfo.devices.selsyn.type.takeIf { it != ModelDeviceType.NONE }?.let { WsDsmartConverterDeviceType.valueOf(it.name) }
            ),
            slagRate = WsDsmartConverterDevicesSlagRate(
                id = modelMeltInfo.devices.slagRate.id.takeIf { it.isNotBlank() },
                name = modelMeltInfo.devices.slagRate.name.takeIf { it.isNotBlank() },
                uri = modelMeltInfo.devices.slagRate.uri.takeIf { it.isNotBlank() },
                deviceType = modelMeltInfo.devices.slagRate.deviceType.takeIf { it.isNotBlank() },
                type = modelMeltInfo.devices.slagRate.type.takeIf { it != ModelDeviceType.NONE }?.let { WsDsmartConverterDeviceType.valueOf(it.name) }
            )
        )
    )

private fun toWsEventListModel(modelEvents: MutableList<ModelEvent>) =
    WsDsmartEventList(
        list = modelEvents.map { event -> toWsEventModel(event) }.toMutableList()
    )

private fun toWsEventModel(event: ModelEvent) =
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

private fun toWsConverterStateModel(context: ConverterBeContext) =
    WsDsmartConverterState(
        meltInfo = toWsConverterMeltInfoModel(context.currentState.get().currentMeltInfo),
        events = toWsEventListModel(context.events),
        warningPoint = context.metalRateWarningPoint
    )
