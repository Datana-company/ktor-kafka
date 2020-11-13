package ru.datana.smart.ui.converter.ws.models

import kotlinx.serialization.Serializable

@Serializable
data class WsDsmartConverterSignalerSound(
    val type: WsDsmartConverterSignalerSound.SignalerSoundTypeModel? = null,
    val interval: Int? = null
) {
    @Serializable
    enum class SignalerSoundTypeModel {
        NONE,
        SOUND_1,
        SOUND_2,
        SOUND_3
    }
}
