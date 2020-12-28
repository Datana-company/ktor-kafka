package ru.datana.smart.ui.converter.common.models

import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

data class CurrentState(
    var currentMeltInfo: ModelMeltInfo = ModelMeltInfo.NONE,
    var lastAngles: ModelAngles = ModelAngles.NONE,
    var slagRates: ConcurrentHashMap<Instant, ModelSlagRate> = ConcurrentHashMap(),
    //var lastSlagRate: ModelSlagRate = ModelSlagRate.NONE,
    var avgStreamRate: Double = Double.MIN_VALUE,
    var lastTimeAngles: Instant = Instant.EPOCH,
    var lastTimeFrame: Instant = Instant.EPOCH
) {
    companion object {
        val NONE = CurrentState()
    }
}
