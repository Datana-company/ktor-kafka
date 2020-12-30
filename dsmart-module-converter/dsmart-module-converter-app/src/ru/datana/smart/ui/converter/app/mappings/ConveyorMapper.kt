package ru.datana.smart.ui.converter.app.mappings

import ru.datana.smart.converter.transport.meta.models.ConverterMeltInfo
import ru.datana.smart.converter.transport.mlui.models.ConverterTransportAngle
import ru.datana.smart.ui.converter.app.common.EventMode
import ru.datana.smart.ui.converter.common.models.ModelEventMode
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.models.*
import ru.datana.smart.ui.extevent.models.ConverterTransportExternalEvent

import java.time.Instant

fun ConverterBeContext.setMeltInfo(converterMeltInfo: ConverterMeltInfo) {
    this.meltInfo = ModelMeltInfo(
        id = converterMeltInfo.id ?: "",
        timeStart = converterMeltInfo.timeStart?.let { Instant.ofEpochMilli(it) } ?: Instant.MIN,
        meltNumber = converterMeltInfo.meltNumber ?: "",
        steelGrade = converterMeltInfo.steelGrade ?: "",
        crewNumber = converterMeltInfo.crewNumber ?: "",
        shiftNumber = converterMeltInfo.shiftNumber ?: "",
        mode = converterMeltInfo.mode?.let { ModelMeltInfo.Mode.valueOf(it.name) } ?: ModelMeltInfo.Mode.NONE,
        devices = ModelMeltDevices(
            converter = ModelDevicesConverter(
                id = converterMeltInfo.devices?.converter?.id ?: "",
                name = converterMeltInfo.devices?.converter?.name ?: "",
                uri = converterMeltInfo.devices?.converter?.uri ?: "",
                deviceType = converterMeltInfo.devices?.converter?.deviceType ?: "",
                type = converterMeltInfo.devices?.converter?.type?.let { ModelDeviceType.valueOf(it.name) }
                    ?: ModelDeviceType.NONE
            ),
            irCamera = ModelDevicesIrCamera(
                id = converterMeltInfo.devices?.irCamera?.id ?: "",
                name = converterMeltInfo.devices?.irCamera?.name ?: "",
                uri = converterMeltInfo.devices?.irCamera?.uri ?: "",
                deviceType = converterMeltInfo.devices?.irCamera?.deviceType ?: "",
                type = converterMeltInfo.devices?.irCamera?.type?.let { ModelDeviceType.valueOf(it.name) }
                    ?: ModelDeviceType.NONE
            ),
            selsyn = ModelDevicesSelsyn(
                id = converterMeltInfo.devices?.selsyn?.id ?: "",
                name = converterMeltInfo.devices?.selsyn?.name ?: "",
                uri = converterMeltInfo.devices?.selsyn?.uri ?: "",
                deviceType = converterMeltInfo.devices?.selsyn?.deviceType ?: "",
                type = converterMeltInfo.devices?.selsyn?.type?.let { ModelDeviceType.valueOf(it.name) }
                    ?: ModelDeviceType.NONE
            ),
            slagRate = ModelDevicesSlagRate(
                id = converterMeltInfo.devices?.slagRate?.id ?: "",
                name = converterMeltInfo.devices?.slagRate?.name ?: "",
                uri = converterMeltInfo.devices?.slagRate?.uri ?: "",
                deviceType = converterMeltInfo.devices?.slagRate?.deviceType ?: "",
                type = converterMeltInfo.devices?.slagRate?.type?.let { ModelDeviceType.valueOf(it.name) }
                    ?: ModelDeviceType.NONE
            )
        )
    )
}

fun ConverterBeContext.setMeltInfo(converterTransportAngle: ConverterTransportAngle) {
    this.meltInfo = ModelMeltInfo(
        id = converterTransportAngle.meltInfo?.id ?: "",
        timeStart = converterTransportAngle.meltInfo?.timeStart?.let { Instant.ofEpochMilli(it) } ?: Instant.MIN,
        meltNumber = converterTransportAngle.meltInfo?.meltNumber ?: "",
        steelGrade = converterTransportAngle.meltInfo?.steelGrade ?: "",
        crewNumber = converterTransportAngle.meltInfo?.crewNumber ?: "",
        shiftNumber = converterTransportAngle.meltInfo?.shiftNumber ?: "",
        mode = converterTransportAngle.meltInfo?.mode?.let { ModelMeltInfo.Mode.valueOf(it.name) }
            ?: ModelMeltInfo.Mode.NONE,
        devices = ModelMeltDevices(
            converter = ModelDevicesConverter(
                id = converterTransportAngle.meltInfo?.devices?.converter?.id ?: "",
                name = converterTransportAngle.meltInfo?.devices?.converter?.name ?: "",
                uri = converterTransportAngle.meltInfo?.devices?.converter?.uri ?: "",
                deviceType = converterTransportAngle.meltInfo?.devices?.converter?.deviceType ?: "",
                type = converterTransportAngle.meltInfo?.devices?.converter?.type?.let { ModelDeviceType.valueOf(it.name) }
                    ?: ModelDeviceType.NONE
            ),
            irCamera = ModelDevicesIrCamera(
                id = converterTransportAngle.meltInfo?.devices?.irCamera?.id ?: "",
                name = converterTransportAngle.meltInfo?.devices?.irCamera?.name ?: "",
                uri = converterTransportAngle.meltInfo?.devices?.irCamera?.uri ?: "",
                deviceType = converterTransportAngle.meltInfo?.devices?.irCamera?.deviceType ?: "",
                type = converterTransportAngle.meltInfo?.devices?.irCamera?.type?.let { ModelDeviceType.valueOf(it.name) }
                    ?: ModelDeviceType.NONE
            ),
            selsyn = ModelDevicesSelsyn(
                id = converterTransportAngle.meltInfo?.devices?.selsyn?.id ?: "",
                name = converterTransportAngle.meltInfo?.devices?.selsyn?.name ?: "",
                uri = converterTransportAngle.meltInfo?.devices?.selsyn?.uri ?: "",
                deviceType = converterTransportAngle.meltInfo?.devices?.selsyn?.deviceType ?: "",
                type = converterTransportAngle.meltInfo?.devices?.selsyn?.type?.let { ModelDeviceType.valueOf(it.name) }
                    ?: ModelDeviceType.NONE
            ),
            slagRate = ModelDevicesSlagRate(
                id = converterTransportAngle.meltInfo?.devices?.slagRate?.id ?: "",
                name = converterTransportAngle.meltInfo?.devices?.slagRate?.name ?: "",
                uri = converterTransportAngle.meltInfo?.devices?.slagRate?.uri ?: "",
                deviceType = converterTransportAngle.meltInfo?.devices?.slagRate?.deviceType ?: "",
                type = converterTransportAngle.meltInfo?.devices?.slagRate?.type?.let { ModelDeviceType.valueOf(it.name) }
                    ?: ModelDeviceType.NONE
            )
        )
    )
}

fun ConverterBeContext.setAngles(converterTransportAngle: ConverterTransportAngle) {
    this.angles = ModelAngles(
        angleTime = converterTransportAngle.angleTime?.let { Instant.ofEpochMilli(it) } ?: Instant.MIN,
        angle = converterTransportAngle.angle ?: Double.MIN_VALUE,
        source = converterTransportAngle.source ?: Double.MIN_VALUE
    )
}

fun ConverterBeContext.setExternalEvent(converterTransportExternalEvent: ConverterTransportExternalEvent) {
    this.externalEvent = ModelEvent(
        alertRuleId = converterTransportExternalEvent.alertRuleId ?: "",
        containerId = converterTransportExternalEvent.containerId ?: "",
        component = converterTransportExternalEvent.component ?: "",
        timestamp = converterTransportExternalEvent.timestamp ?: "",
        level = converterTransportExternalEvent.level ?: "",
        loggerName = converterTransportExternalEvent.loggerName ?: "",
        textMessage = converterTransportExternalEvent.message ?: ""
    )
}

fun toEventMode(eventMode: EventMode) =
    ModelEventMode.valueOf(eventMode.name)

