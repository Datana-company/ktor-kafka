package ru.datana.smart.ui.converter.common.models

data class ModelDevicesIrCamera (
    val id: String? = null,
    val name: String? = null,
    val uri: String? = null,
    val type: Type? = null
) {
    enum class Type(val value: String) {
        DEVICE("device"),
        FILE("file");
    }
}
