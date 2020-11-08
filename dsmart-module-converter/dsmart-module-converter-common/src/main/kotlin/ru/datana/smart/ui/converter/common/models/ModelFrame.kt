package ru.datana.smart.ui.converter.common.models

data class ModelFrame(
    var frameId: String? = "",
    var frameTime: Long? = Long.MIN_VALUE,
    var framePath: String? = "",
    var image: String? = "",
    var channel: Channels? = Channels.NONE
) {

    companion object {
        val NONE = ModelFrame()
    }

    enum class Channels {
        NONE,
        CAMERA,
        MATH,
        MATH_TREATED
    }
}
