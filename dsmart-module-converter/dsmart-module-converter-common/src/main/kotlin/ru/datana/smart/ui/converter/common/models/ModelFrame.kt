package ru.datana.smart.ui.converter.common.models

data class ModelFrame(
    val frameId: String? = null,
    val frameTime: Long? = null,
    val framePath: String? = null
) {

    companion object {
        val NONE = ModelFrame()
    }
}
