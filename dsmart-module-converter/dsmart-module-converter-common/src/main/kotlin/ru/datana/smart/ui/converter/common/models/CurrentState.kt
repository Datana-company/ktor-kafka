package ru.datana.smart.ui.converter.common.models

data class CurrentState(
    var currentMeltInfo: ModelMeltInfo = ModelMeltInfo.NONE,
    var lastAngles: ModelAngles = ModelAngles.NONE,
    var lastSlagRate: ModelSlagRate = ModelSlagRate.NONE, //не используется
    var avgSlagRate: ModelSlagRate = ModelSlagRate.NONE // double сейчас
) {
    companion object {
        val NONE = CurrentState()
    }
}
