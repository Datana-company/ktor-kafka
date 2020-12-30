package ru.datana.smart.ui.converter.common.models

import java.time.Instant

data class ModelSlagRate(
    var slagRateTime: Instant = Instant.MIN,
    var steelRate: Double = Double.MIN_VALUE,
    var slagRate: Double = Double.MIN_VALUE,
    var avgSlagRate: Double = Double.MIN_VALUE
) {

    companion object {
        val NONE = ModelSlagRate()
    }
}
