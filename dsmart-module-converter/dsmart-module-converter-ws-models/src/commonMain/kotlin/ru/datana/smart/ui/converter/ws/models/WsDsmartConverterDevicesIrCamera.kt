package ru.datana.smart.ui.converter.ws.models

import kotlinx.serialization.Serializable

@Serializable
data class WsDsmartConverterDevicesIrCamera (
    val id: String? = null,
    val name: String? = null,
    val uri: String? = null,
    val type: WsDsmartConverterDevicesIrCamera.Type? = null
) {
    @Serializable
    enum class Type(val value: String){
        DEVICE("device"),
        FILE("file");
    }
}
