package ru.datana.smart.ui.converter.ws.models

import kotlinx.serialization.Serializable

@Serializable
data class WsDsmartConverterSignaler(
    val level: WsDsmartConverterSignaler.SignalerLevelModel? = null,
    val sound: WsDsmartConverterSignalerSound? = null
) {
    @Serializable
    enum class SignalerLevelModel {
        NONE,
        NO_SIGNAL,
        INFO,
        WARNING,
        CRITICAL
    }
}
