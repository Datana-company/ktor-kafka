package ru.datana.smart.ui.converter.repository.inmemory.currentstate

import ru.datana.smart.ui.converter.common.models.ModelSlagRate

data class CurrentStateInMemorySlagRate(
    val steelRate: Double? = null,
    val slagRate: Double? = null
) {
    fun toModel() = ModelSlagRate(
        steelRate = steelRate?: Double.MIN_VALUE,
        slagRate = slagRate?: Double.MIN_VALUE
    )

    companion object {
        fun of(model: ModelSlagRate) = CurrentStateInMemorySlagRate(
            steelRate = model.steelRate.takeIf { it != Double.MIN_VALUE },
            slagRate = model.slagRate.takeIf { it != Double.MIN_VALUE }
        )
    }
}
