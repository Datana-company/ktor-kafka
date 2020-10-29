package ru.datana.smart.ui.converter.ws.models

import kotlinx.serialization.Serializable

@Serializable
enum class WsDsmartConverterDeviceType(val value: String) {
    DEVICE("device"),
    FILE("file"),
    COMPUTATION("computation");
}
