package ru.datana.smart.ui.converter.common.models

data class ModelDevicesSlagRate(
    override var id: String = "",
    override var name: String = "",
    override var uri: String = "",
    override var deviceType: String = "",
    override var type: ModelDeviceType = ModelDeviceType.NONE
): ModelDevice {
    companion object {
        val NONE = ModelDevicesSlagRate()
    }
}
