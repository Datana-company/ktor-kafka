package ru.datana.smart.ui.converter.app.mappings

import ru.datana.smart.ui.converter.common.models.*
import ru.datana.smart.ui.meta.models.ConverterMeltInfo
import ru.datana.smart.ui.mlui.models.ConverterTransportMlUi
import ru.datana.smart.ui.viml.models.ConverterTransportViMl
import ru.datana.smart.ui.mlui.models.ConverterTransportAngle

fun toModelMeltInfo(converterMeltInfo: ConverterMeltInfo) =
    ModelMeltInfo(
        id = converterMeltInfo.id ?: "",
        timeStart = converterMeltInfo.timeStart ?: Long.MIN_VALUE,
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
                type = converterMeltInfo.devices?.converter?.type?.let { ModelDeviceType.valueOf(it.name) } ?: ModelDeviceType.NONE
            ),
            irCamera = ModelDevicesIrCamera(
                id = converterMeltInfo.devices?.irCamera?.id ?: "",
                name = converterMeltInfo.devices?.irCamera?.name ?: "",
                uri = converterMeltInfo.devices?.irCamera?.uri ?: "",
                deviceType = converterMeltInfo.devices?.irCamera?.deviceType ?: "",
                type = converterMeltInfo.devices?.irCamera?.type?.let { ModelDeviceType.valueOf(it.name) } ?: ModelDeviceType.NONE
            ),
            selsyn = ModelDevicesSelsyn(
                id = converterMeltInfo.devices?.selsyn?.id ?: "",
                name = converterMeltInfo.devices?.selsyn?.name ?: "",
                uri = converterMeltInfo.devices?.selsyn?.uri ?: "",
                deviceType = converterMeltInfo.devices?.selsyn?.deviceType ?: "",
                type = converterMeltInfo.devices?.selsyn?.type?.let { ModelDeviceType.valueOf(it.name) } ?: ModelDeviceType.NONE
            ),
            slagRate = ModelDevicesSlagRate(
                id = converterMeltInfo.devices?.slagRate?.id ?: "",
                name = converterMeltInfo.devices?.slagRate?.name ?: "",
                uri = converterMeltInfo.devices?.slagRate?.uri ?: "",
                deviceType = converterMeltInfo.devices?.slagRate?.deviceType ?: "",
                type = converterMeltInfo.devices?.slagRate?.type?.let { ModelDeviceType.valueOf(it.name) } ?: ModelDeviceType.NONE
            )
        )
    )

fun toModelMeltInfo(converterTransportViMl: ConverterTransportViMl) =
    ModelMeltInfo(
        id = converterTransportViMl.meltInfo?.id ?: "",
        timeStart = converterTransportViMl.meltInfo?.timeStart ?: Long.MIN_VALUE,
        meltNumber = converterTransportViMl.meltInfo?.meltNumber ?: "",
        steelGrade = converterTransportViMl.meltInfo?.steelGrade ?: "",
        crewNumber = converterTransportViMl.meltInfo?.crewNumber ?: "",
        shiftNumber = converterTransportViMl.meltInfo?.shiftNumber ?: "",
        mode = converterTransportViMl.meltInfo?.mode?.let { ModelMeltInfo.Mode.valueOf(it.name) } ?: ModelMeltInfo.Mode.NONE,
        devices = ModelMeltDevices(
            converter = ModelDevicesConverter(
                id = converterTransportViMl.meltInfo?.devices?.converter?.id ?: "",
                name = converterTransportViMl.meltInfo?.devices?.converter?.name ?: "",
                uri = converterTransportViMl.meltInfo?.devices?.converter?.uri ?: "",
                deviceType = converterTransportViMl.meltInfo?.devices?.converter?.deviceType ?: "",
                type = converterTransportViMl.meltInfo?.devices?.converter?.type?.let { ModelDeviceType.valueOf(it.name) } ?: ModelDeviceType.NONE
            ),
            irCamera = ModelDevicesIrCamera(
                id = converterTransportViMl.meltInfo?.devices?.irCamera?.id ?: "",
                name = converterTransportViMl.meltInfo?.devices?.irCamera?.name ?: "",
                uri = converterTransportViMl.meltInfo?.devices?.irCamera?.uri ?: "",
                deviceType = converterTransportViMl.meltInfo?.devices?.irCamera?.deviceType ?: "",
                type = converterTransportViMl.meltInfo?.devices?.irCamera?.type?.let { ModelDeviceType.valueOf(it.name) } ?: ModelDeviceType.NONE
            ),
            selsyn = ModelDevicesSelsyn(
                id = converterTransportViMl.meltInfo?.devices?.selsyn?.id ?: "",
                name = converterTransportViMl.meltInfo?.devices?.selsyn?.name ?: "",
                uri = converterTransportViMl.meltInfo?.devices?.selsyn?.uri ?: "",
                deviceType = converterTransportViMl.meltInfo?.devices?.selsyn?.deviceType ?: "",
                type = converterTransportViMl.meltInfo?.devices?.selsyn?.type?.let { ModelDeviceType.valueOf(it.name) } ?: ModelDeviceType.NONE
            ),
            slagRate = ModelDevicesSlagRate(
                id = converterTransportViMl.meltInfo?.devices?.slagRate?.id ?: "",
                name = converterTransportViMl.meltInfo?.devices?.slagRate?.name ?: "",
                uri = converterTransportViMl.meltInfo?.devices?.slagRate?.uri ?: "",
                deviceType = converterTransportViMl.meltInfo?.devices?.slagRate?.deviceType ?: "",
                type = converterTransportViMl.meltInfo?.devices?.slagRate?.type?.let { ModelDeviceType.valueOf(it.name) } ?: ModelDeviceType.NONE
            )
        )
    )

fun toModelMeltInfo(converterTransportMlUi: ConverterTransportMlUi) =
    ModelMeltInfo(
        id = converterTransportMlUi.meltInfo?.id ?: "",
        timeStart = converterTransportMlUi.meltInfo?.timeStart ?: Long.MIN_VALUE,
        meltNumber = converterTransportMlUi.meltInfo?.meltNumber ?: "",
        steelGrade = converterTransportMlUi.meltInfo?.steelGrade ?: "",
        crewNumber = converterTransportMlUi.meltInfo?.crewNumber ?: "",
        shiftNumber = converterTransportMlUi.meltInfo?.shiftNumber ?: "",
        mode = converterTransportMlUi.meltInfo?.mode?.let { ModelMeltInfo.Mode.valueOf(it.name) } ?: ModelMeltInfo.Mode.NONE,
        devices = ModelMeltDevices(
            converter = ModelDevicesConverter(
                id = converterTransportMlUi.meltInfo?.devices?.converter?.id ?: "",
                name = converterTransportMlUi.meltInfo?.devices?.converter?.name ?: "",
                uri = converterTransportMlUi.meltInfo?.devices?.converter?.uri ?: "",
                deviceType = converterTransportMlUi.meltInfo?.devices?.converter?.deviceType ?: "",
                type = converterTransportMlUi.meltInfo?.devices?.converter?.type?.let { ModelDeviceType.valueOf(it.name) } ?: ModelDeviceType.NONE
            ),
            irCamera = ModelDevicesIrCamera(
                id = converterTransportMlUi.meltInfo?.devices?.irCamera?.id ?: "",
                name = converterTransportMlUi.meltInfo?.devices?.irCamera?.name ?: "",
                uri = converterTransportMlUi.meltInfo?.devices?.irCamera?.uri ?: "",
                deviceType = converterTransportMlUi.meltInfo?.devices?.irCamera?.deviceType ?: "",
                type = converterTransportMlUi.meltInfo?.devices?.irCamera?.type?.let { ModelDeviceType.valueOf(it.name) } ?: ModelDeviceType.NONE
            ),
            selsyn = ModelDevicesSelsyn(
                id = converterTransportMlUi.meltInfo?.devices?.selsyn?.id ?: "",
                name = converterTransportMlUi.meltInfo?.devices?.selsyn?.name ?: "",
                uri = converterTransportMlUi.meltInfo?.devices?.selsyn?.uri ?: "",
                deviceType = converterTransportMlUi.meltInfo?.devices?.selsyn?.deviceType ?: "",
                type = converterTransportMlUi.meltInfo?.devices?.selsyn?.type?.let { ModelDeviceType.valueOf(it.name) } ?: ModelDeviceType.NONE
            ),
            slagRate = ModelDevicesSlagRate(
                id = converterTransportMlUi.meltInfo?.devices?.slagRate?.id ?: "",
                name = converterTransportMlUi.meltInfo?.devices?.slagRate?.name ?: "",
                uri = converterTransportMlUi.meltInfo?.devices?.slagRate?.uri ?: "",
                deviceType = converterTransportMlUi.meltInfo?.devices?.slagRate?.deviceType ?: "",
                type = converterTransportMlUi.meltInfo?.devices?.slagRate?.type?.let { ModelDeviceType.valueOf(it.name) } ?: ModelDeviceType.NONE
            )
        )
    )

fun toModelMeltInfo(converterTransportAngle: ConverterTransportAngle) =
    ModelMeltInfo(
        id = converterTransportAngle.meltInfo?.id ?: "",
        timeStart = converterTransportAngle.meltInfo?.timeStart ?: Long.MIN_VALUE,
        meltNumber = converterTransportAngle.meltInfo?.meltNumber ?: "",
        steelGrade = converterTransportAngle.meltInfo?.steelGrade ?: "",
        crewNumber = converterTransportAngle.meltInfo?.crewNumber ?: "",
        shiftNumber = converterTransportAngle.meltInfo?.shiftNumber ?: "",
        mode = converterTransportAngle.meltInfo?.mode?.let { ModelMeltInfo.Mode.valueOf(it.name) } ?: ModelMeltInfo.Mode.NONE,
        devices = ModelMeltDevices(
            converter = ModelDevicesConverter(
                id = converterTransportAngle.meltInfo?.devices?.converter?.id ?: "",
                name = converterTransportAngle.meltInfo?.devices?.converter?.name ?: "",
                uri = converterTransportAngle.meltInfo?.devices?.converter?.uri ?: "",
                deviceType = converterTransportAngle.meltInfo?.devices?.converter?.deviceType ?: "",
                type = converterTransportAngle.meltInfo?.devices?.converter?.type?.let { ModelDeviceType.valueOf(it.name) } ?: ModelDeviceType.NONE
            ),
            irCamera = ModelDevicesIrCamera(
                id = converterTransportAngle.meltInfo?.devices?.irCamera?.id ?: "",
                name = converterTransportAngle.meltInfo?.devices?.irCamera?.name ?: "",
                uri = converterTransportAngle.meltInfo?.devices?.irCamera?.uri ?: "",
                deviceType = converterTransportAngle.meltInfo?.devices?.irCamera?.deviceType ?: "",
                type = converterTransportAngle.meltInfo?.devices?.irCamera?.type?.let { ModelDeviceType.valueOf(it.name) } ?: ModelDeviceType.NONE
            ),
            selsyn = ModelDevicesSelsyn(
                id = converterTransportAngle.meltInfo?.devices?.selsyn?.id ?: "",
                name = converterTransportAngle.meltInfo?.devices?.selsyn?.name ?: "",
                uri = converterTransportAngle.meltInfo?.devices?.selsyn?.uri ?: "",
                deviceType = converterTransportAngle.meltInfo?.devices?.selsyn?.deviceType ?: "",
                type = converterTransportAngle.meltInfo?.devices?.selsyn?.type?.let { ModelDeviceType.valueOf(it.name) } ?: ModelDeviceType.NONE
            ),
            slagRate = ModelDevicesSlagRate(
                id = converterTransportAngle.meltInfo?.devices?.slagRate?.id ?: "",
                name = converterTransportAngle.meltInfo?.devices?.slagRate?.name ?: "",
                uri = converterTransportAngle.meltInfo?.devices?.slagRate?.uri ?: "",
                deviceType = converterTransportAngle.meltInfo?.devices?.slagRate?.deviceType ?: "",
                type = converterTransportAngle.meltInfo?.devices?.slagRate?.type?.let { ModelDeviceType.valueOf(it.name) } ?: ModelDeviceType.NONE
            )
        )
    )

fun toModelFrame(converterTransportViMl: ConverterTransportViMl) =
    ModelFrame(
        frameId = converterTransportViMl.frameId ?: "",
        frameTime = converterTransportViMl.frameTime ?: Long.MIN_VALUE,
        framePath = converterTransportViMl.framePath ?: ""
    )

fun toModelFrame(converterTransportMlUi: ConverterTransportMlUi) =
    // будут браться другие поля, когда они появятся
    ModelFrame(
        frameId = converterTransportMlUi.frameId ?: "",
        frameTime = converterTransportMlUi.frameTime ?: Long.MIN_VALUE,
        framePath = converterTransportMlUi.framePath ?: ""
    )

fun toModelSlagRate(converterTransportMlUi: ConverterTransportMlUi) =
    ModelSlagRate(
        steelRate = converterTransportMlUi.steelRate ?: Double.MIN_VALUE,
        slagRate = converterTransportMlUi.slagRate ?: Double.MIN_VALUE
    )

fun toModelAngles(converterTransportAngle: ConverterTransportAngle) =
    ModelAngles(
        angleTime = converterTransportAngle.angleTime ?: Long.MIN_VALUE,
        angle = converterTransportAngle.angle ?: Double.MIN_VALUE,
        source = converterTransportAngle.source ?: Double.MIN_VALUE
    )
