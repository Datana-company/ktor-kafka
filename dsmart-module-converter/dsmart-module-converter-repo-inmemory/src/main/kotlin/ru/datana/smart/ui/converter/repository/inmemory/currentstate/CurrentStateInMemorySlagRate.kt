package ru.datana.smart.ui.converter.repository.inmemory.currentstate

import ru.datana.smart.ui.converter.common.models.ModelSlagRate
import java.time.Instant

data class CurrentStateInMemorySlagRate(
    val slagRateTime: Long? = null,
    val steelRate: Double? = null,
    val slagRate: Double? = null,
    val avgSteelRate: Double? = null,
    val avgSlagRate: Double? = null
) {
    fun toModel() = ModelSlagRate(
        slagRateTime = slagRateTime?.let { Instant.ofEpochMilli(it) } ?: Instant.MIN,
        steelRate = steelRate?: Double.MIN_VALUE,
        slagRate = slagRate?: Double.MIN_VALUE,
        avgSteelRate = avgSteelRate?: Double.MIN_VALUE,
        avgSlagRate = avgSlagRate?: Double.MIN_VALUE
    )

    companion object {
        fun of(model: ModelSlagRate) = CurrentStateInMemorySlagRate(
            slagRateTime = model.slagRateTime.takeIf { it != Instant.MIN }?.toEpochMilli(),
            steelRate = model.steelRate.takeIf { it != Double.MIN_VALUE },
            slagRate = model.slagRate.takeIf { it != Double.MIN_VALUE },
            avgSteelRate = model.avgSteelRate.takeIf { it != Double.MIN_VALUE },
            avgSlagRate = model.avgSlagRate.takeIf { it != Double.MIN_VALUE }
        )
    }
}
