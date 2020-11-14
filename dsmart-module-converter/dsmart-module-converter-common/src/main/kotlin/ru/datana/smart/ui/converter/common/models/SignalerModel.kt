package ru.datana.smart.ui.converter.common.models

data class SignalerModel(
    var level: SignalerLevelModel = SignalerLevelModel.NONE,
    var sound: SignalerSoundModel = SignalerSoundModel.NONE
) {
    companion object {
        val NONE = SignalerModel()
    }

    enum class SignalerLevelModel {
        NONE,
        INFO,
        WARNING,
        CRITICAL
    }
}
