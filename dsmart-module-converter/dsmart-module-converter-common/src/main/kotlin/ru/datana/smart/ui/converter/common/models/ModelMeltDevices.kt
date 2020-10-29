package ru.datana.smart.ui.converter.common.models

data class ModelMeltDevices(
    val converter: ModelDevicesConverter? = null,
    val irCamera: ModelDevicesIrCamera? = null,
    val selsyn: ModelDevicesSelsyn? = null,
    val slagRate: ModelDevicesSlagRate? = null
)
