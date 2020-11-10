package ru.datana.smart.ui.converter.common.models

class ModelAngles(
    var angleTime: Long = Long.MIN_VALUE,
    var angle: Double = Double.MIN_VALUE,
    var source: Double = Double.MIN_VALUE
) {

    companion object {
        val NONE = ModelAngles()
    }
}
