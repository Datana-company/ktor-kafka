package ru.datana.smart.ui.converter.angle.app.mappings

import org.apache.kafka.clients.producer.ProducerRecord
import ru.datana.smart.converter.transport.mlui.models.*
import ru.datana.smart.ui.converter.angle.app.models.AngleMessage
import ru.datana.smart.ui.converter.angle.app.models.ConverterAngContext
import ru.datana.smart.ui.converter.common.models.ModelDeviceType
import ru.datana.smart.ui.converter.common.models.ModelMeltInfo

fun ConverterAngContext.toAngleMessage(): ProducerRecord<String, String>? = if (angleMessage != AngleMessage.NONE)
    ProducerRecord(
        topicAngle,
        "",
        jacksonSerializer.writeValueAsString(
            ConverterTransportAngle(
                angleTime = frame.frameTime.toEpochMilli(),
                angle = angleMessage.angle,
                meltInfo = ConverterMeltInfo(
                    id = meltInfo.id.takeIf { it.isNotBlank() },
                    timeStart = meltInfo.timeStart.toEpochMilli(),
                    meltNumber = meltInfo.meltNumber.takeIf { it.isNotBlank() },
                    steelGrade = meltInfo.steelGrade.takeIf { it.isNotBlank() },
                    crewNumber = meltInfo.crewNumber.takeIf { it.isNotBlank() },
                    shiftNumber = meltInfo.shiftNumber.takeIf { it.isNotBlank() },
                    mode = meltInfo.mode.toTransport(),
                    devices = ConverterMeltDevices(
                        converter = ConverterDevicesConverter(
                            id = meltInfo.devices.converter.id.takeIf { it.isNotBlank() },
                            name = meltInfo.devices.converter.name.takeIf { it.isNotBlank() },
                            uri = meltInfo.devices.converter.uri.takeIf { it.isNotBlank() },
                            deviceType = ConverterDevicesConverter::class.java.simpleName,
                            type = meltInfo.devices.converter.type.toTransport()
                        ),
                        irCamera = ConverterDevicesIrCamera(
                            id = meltInfo.devices.irCamera.id.takeIf { it.isNotBlank() },
                            name = meltInfo.devices.irCamera.name.takeIf { it.isNotBlank() },
                            uri = meltInfo.devices.irCamera.uri.takeIf { it.isNotBlank() },
                            deviceType = ConverterDevicesIrCamera::class.java.simpleName,
                            type = meltInfo.devices.irCamera.type.toTransport()
                        ),
                        selsyn = ConverterDevicesSelsyn(
                            id = meltInfo.devices.selsyn.id.takeIf { it.isNotBlank() },
                            name = meltInfo.devices.selsyn.name.takeIf { it.isNotBlank() },
                            uri = meltInfo.devices.selsyn.uri.takeIf { it.isNotBlank() },
                            deviceType = ConverterDevicesSelsyn::class.java.simpleName,
                            type = meltInfo.devices.selsyn.type.toTransport()
                        ),
                        slagRate = ConverterDevicesSlagRate(
                            id = meltInfo.devices.slagRate.id.takeIf { it.isNotBlank() },
                            name = meltInfo.devices.slagRate.name.takeIf { it.isNotBlank() },
                            uri = meltInfo.devices.slagRate.uri.takeIf { it.isNotBlank() },
                            deviceType = ConverterDevicesSlagRate::class.java.simpleName,
                            type = meltInfo.devices.slagRate.type.toTransport()
                        )
                    )
                )
            )
        )
    ) else null

private fun ModelDeviceType.toTransport(): ConverterDeviceType? = when(this) {
    ModelDeviceType.COMPUTATION -> ConverterDeviceType.COMPUTATION
    ModelDeviceType.DEVICE -> ConverterDeviceType.DEVICE
    ModelDeviceType.FILE -> ConverterDeviceType.FILE
    else -> null
}

private fun ModelMeltInfo.Mode?.toTransport(): ConverterWorkMode? = when(this) {
    ModelMeltInfo.Mode.EMULATION -> ConverterWorkMode.EMULATION
    ModelMeltInfo.Mode.PROD -> ConverterWorkMode.PROD
    ModelMeltInfo.Mode.TEST -> ConverterWorkMode.TEST
    else -> null
}
