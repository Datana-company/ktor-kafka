package ru.datana.smart.ui.converter.common.models

data class ModelSlagRate(
    val steelRate: Double = Double.MIN_VALUE,
    val slagRate: Double = Double.MIN_VALUE
) {

    companion object {
        val NONE = ModelSlagRate()
    }
}
