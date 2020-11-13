package ru.datana.smart.ui.converter.common.models

data class SignalerSoundModel(
    var type: SignalerSoundTypeModel = SignalerSoundTypeModel.NONE,
    var interval: Int = Int.MIN_VALUE
) {
    companion object {
        val NONE = SignalerSoundModel()
    }

    enum class SignalerSoundTypeModel {
        NONE,
        SOUND_1,
        SOUND_2,
        SOUND_3
    }
}
