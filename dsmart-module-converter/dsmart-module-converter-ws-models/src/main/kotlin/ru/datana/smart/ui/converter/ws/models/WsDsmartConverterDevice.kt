package ru.datana.smart.ui.converter.ws.models

interface WsDsmartConverterDevice {
    val id: String?
    val name: String?
    val uri: String?
    val deviceType: String?
    val type: WsDsmartConverterDeviceType?
}
