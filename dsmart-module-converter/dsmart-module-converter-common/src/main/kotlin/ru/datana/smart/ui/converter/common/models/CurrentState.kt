package ru.datana.smart.ui.converter.common.models

import java.time.Instant

data class CurrentState(
    var currentMeltInfo: ModelMeltInfo = ModelMeltInfo.NONE,
    var lastAngles: ModelAngles = ModelAngles.NONE,
    var slagRateList: MutableList<ModelSlagRate> = mutableListOf(),
    var lastAvgSteelRate: Double = Double.MIN_VALUE,
    var lastAvgSlagRate: Double = Double.MIN_VALUE,
    var lastTimeAngles: Instant = Instant.MIN,
    var lastTimeFrame: Instant = Instant.MIN
) {
    companion object {
        val NONE = CurrentState()
    }
}
