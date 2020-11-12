package ru.datana.smart.ui.converter.ws.models

import kotlinx.serialization.Serializable

@Serializable
data class WsDsmartConverterSignalerSound(
    val type: WsDsmartConverterSignalerSound.SignalerSoundTypeModel? = null,
    val interval: Int? = null
) {
    @Serializable
    enum class SignalerSoundTypeModel(val value: String) {
        NONE("NONE"),
        SOUND_1("SOUND_1"),
        SOUND_2("SOUND_2"),
        SOUND_3("SOUND_3")
    }
}
