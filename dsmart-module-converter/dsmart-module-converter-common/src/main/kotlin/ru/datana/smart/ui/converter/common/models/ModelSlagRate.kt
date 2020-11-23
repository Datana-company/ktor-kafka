package ru.datana.smart.ui.converter.common.models

data class ModelSlagRate(
    var steelRate: Double = Double.MIN_VALUE,
    var slagRate: Double = Double.MIN_VALUE,
    var avgSteelRate: Double = Double.MIN_VALUE
) {

    companion object {
        val NONE = ModelSlagRate()
    }
}
