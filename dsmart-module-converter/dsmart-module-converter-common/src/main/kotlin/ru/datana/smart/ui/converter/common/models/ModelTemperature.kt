package ru.datana.smart.ui.converter.common.models

class ModelTemperature(
    val temperatureAverage: Double? = null
) {

    companion object {
        val NONE = ModelTemperature()
    }
}
