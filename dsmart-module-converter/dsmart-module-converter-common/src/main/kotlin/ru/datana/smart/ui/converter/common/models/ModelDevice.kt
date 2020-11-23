package ru.datana.smart.ui.converter.common.models

interface ModelDevice {
    var id: String
    var name: String
    var uri: String
    var deviceType: String
    var type: ModelDeviceType
}
