package ru.datana.smart.ui.converter.common.models

import java.time.Instant

data class CurrentState(
    var currentMeltInfo: ModelMeltInfo = ModelMeltInfo.NONE,
    var lastAngles: ModelAngles = ModelAngles.NONE,
    var lastSlagRate: ModelSlagRate = ModelSlagRate.NONE,
    var avgStreamRate: Double = Double.MIN_VALUE,
    var lastTimeAngles: Instant = Instant.EPOCH,
    var lastTimeFrame: Instant = Instant.EPOCH
) {
    companion object {
        val NONE = CurrentState()
    }
}
