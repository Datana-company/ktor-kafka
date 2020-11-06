package ru.datana.smart.ui.converter.common.models

data class ModelSlagRate(
    val slagRateTime: Long? = null,
    val steelRate: Double? = null,
    val slagRate: Double? = null
) {

    companion object {
        val NONE = ModelSlagRate()
    }
}
