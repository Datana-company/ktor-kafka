package ru.datana.smart.ui.converter.common.models

class ModelAngles(
    val angleTime: Long? = null,
    val angle: Double? = null,
    val source: Double? = null
) {

    companion object {
        val NONE = ModelAngles()
    }
}
