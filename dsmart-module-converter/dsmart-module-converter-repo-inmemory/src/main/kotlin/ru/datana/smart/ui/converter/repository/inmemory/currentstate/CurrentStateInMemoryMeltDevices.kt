package ru.datana.smart.ui.converter.repository.inmemory.currentstate

import ru.datana.smart.ui.converter.common.models.*


data class CurrentStateInMemoryMeltDevices(
    val converter: CurrentStateInMemoryDevicesConverter? = null,
    val irCamera: CurrentStateInMemoryDevicesIrCamera? = null,
    val selsyn: CurrentStateInMemoryDevicesSelsyn? = null,
    val slagRate: CurrentStateInMemoryDevicesSlagRate? = null
) {
    fun toModel() = ModelMeltDevices(
        converter = converter?.toModel()?: ModelDevicesConverter.NONE,
        irCamera = irCamera?.toModel()?: ModelDevicesIrCamera.NONE,
        selsyn = selsyn?.toModel()?: ModelDevicesSelsyn.NONE,
        slagRate = slagRate?.toModel()?: ModelDevicesSlagRate.NONE
    )

    companion object {
        fun of(model: ModelMeltDevices) = CurrentStateInMemoryMeltDevices(
            converter = model.converter.takeIf { it != ModelDevicesConverter.NONE }?.let { CurrentStateInMemoryDevicesConverter.of(it) },
            irCamera = model.irCamera.takeIf { it != ModelDevicesIrCamera.NONE }?.let { CurrentStateInMemoryDevicesIrCamera.of(it) },
            selsyn = model.selsyn.takeIf { it != ModelDevicesSelsyn.NONE }?.let { CurrentStateInMemoryDevicesSelsyn.of(it) },
            slagRate = model.slagRate.takeIf { it != ModelDevicesSlagRate.NONE }?.let { CurrentStateInMemoryDevicesSlagRate.of(it) }
        )
    }
}

