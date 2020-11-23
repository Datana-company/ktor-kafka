package ru.datana.smart.ui.converter.common.models

data class ModelMeltDevices(
    var converter: ModelDevicesConverter = ModelDevicesConverter.NONE,
    var irCamera: ModelDevicesIrCamera = ModelDevicesIrCamera.NONE,
    var selsyn: ModelDevicesSelsyn = ModelDevicesSelsyn.NONE,
    var slagRate: ModelDevicesSlagRate = ModelDevicesSlagRate.NONE
) {
    companion object {
        val NONE = ModelMeltDevices()
    }
}
