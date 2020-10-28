package ru.datana.smart.ui.converter.common.models

class ModelSlagRate(
    val steelRate: Double? = null,
    val slagRate: Double? = null
) {

    companion object {
        val NONE = ModelSlagRate()
    }
}
