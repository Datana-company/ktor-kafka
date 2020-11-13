package ru.datana.smart.ui.converter.common.models

data class SignalerSoundModel(
    var type: SignalerSoundTypeModel = SignalerSoundTypeModel.NONE,
    var interval: Int = Int.MIN_VALUE
) {
    companion object {
        val NONE = SignalerSoundModel()
    }

    enum class SignalerSoundTypeModel(val value: String) {
        NONE("NONE"),
        SOUND_1("SOUND_1"),
        SOUND_2("SOUND_2"),
        SOUND_3("SOUND_3")
    }
}
