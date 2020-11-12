package ru.datana.smart.ui.converter.common.models

data class SignalerModel(
    var level: SignalerLevelModel = SignalerLevelModel.INFO,
    var sound: SignalerSoundModel = SignalerSoundModel.NONE
) {
    companion object {
        val NONE = SignalerModel()
    }

    enum class SignalerLevelModel(val value: String) {
        INFO("INFO"),
        WARNING("WARNING"),
        CRITICAL("CRITICAL")
    }
}
