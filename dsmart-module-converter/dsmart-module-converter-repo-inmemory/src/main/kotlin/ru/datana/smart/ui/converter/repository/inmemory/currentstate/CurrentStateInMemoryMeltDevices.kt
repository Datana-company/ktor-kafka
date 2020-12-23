package ru.datana.smart.ui.converter.repository.inmemory.currentstate


data class CurrentStateInMemoryMeltDevices(
    val converter: CurrentStateInMemoryDevicesConverter? = null,
    val irCamera: CurrentStateInMemoryDevicesIrCamera? = null,
    val selsyn: CurrentStateInMemoryDevicesSelsyn? = null,
    val slagRate: CurrentStateInMemoryDevicesSlagRate? = null
) {

}
