package ru.datana.smart.ui.converter.common.models

data class CurrentState(
    var currentMeltInfo: ModelMeltInfo = ModelMeltInfo.NONE,
    var lastAngles: ModelAngles = ModelAngles.NONE,
    var lastSlagRate: ModelSlagRate = ModelSlagRate.NONE,
    var avgStreamRate: Double = Double.MIN_VALUE
) {
    companion object {
        val NONE = CurrentState()
    }
}
