package ru.datana.smart.ui.converter.common.models

import java.time.Instant

data class CurrentState(
    var currentMeltInfo: ModelMeltInfo = ModelMeltInfo.NONE,
    var lastAngles: ModelAngles = ModelAngles.NONE,
    var slagRateList: MutableList<ModelSlagRate> = mutableListOf(),
    var lastAvgSlagRate: Double = Double.MIN_VALUE,
    var lastTimeAngles: Instant = Instant.EPOCH,
    var lastTimeFrame: Instant = Instant.EPOCH
) {
    companion object {
        val NONE = CurrentState()
    }
}
