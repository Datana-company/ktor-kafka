package ru.datana.smart.ui.converter.common.models

import java.time.Instant

class ModelAngles(
    var angleTime: Instant = Instant.MIN,
    var angle: Double = Double.MIN_VALUE,
    var source: Double = Double.MIN_VALUE
) {

    companion object {
        val NONE = ModelAngles()
    }
}
