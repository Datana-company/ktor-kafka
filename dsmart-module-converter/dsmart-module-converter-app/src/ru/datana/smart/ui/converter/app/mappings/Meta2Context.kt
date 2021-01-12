package ru.datana.smart.ui.converter.app.mappings

import ru.datana.smart.common.ktor.kafka.ConsumerItem
import ru.datana.smart.converter.transport.meta.models.ConverterMeltInfo
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.exceptions.ConverterDeserializationException
import ru.datana.smart.ui.converter.common.models.*
import java.time.Instant

fun ConverterBeContext.of(item: ConsumerItem<String, String>): ConverterBeContext {
    val transporModel = fromConsumerItem(item)
    setMeltInfo(transporModel)
    return this
}

fun fromConsumerItem(item: ConsumerItem<String, String>): ConverterMeltInfo {
    try {
        return jacksonSerializer.readValue(item.value, ConverterMeltInfo::class.java)!!
    } catch (e: Exception) {
        throw ConverterDeserializationException(
            message = e.message,
            cause = e.cause,
            source = item.value
        )
    }
}

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
