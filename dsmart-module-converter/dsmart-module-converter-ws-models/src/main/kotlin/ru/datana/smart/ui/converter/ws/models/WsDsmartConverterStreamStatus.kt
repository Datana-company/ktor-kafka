package ru.datana.smart.ui.converter.ws.models

import kotlinx.serialization.Serializable

@Serializable
data class WsDsmartConverterStreamStatus(
    val status: StreamStatus? = null
) {
    @Serializable
    enum class StreamStatus(val value: String) {
        CRITICAL("CRITICAL"),
        WARNING("WARNING"),
        INFO("INFO"),
        NORMAL("NORMAL"),
        END("END")
    }
}
