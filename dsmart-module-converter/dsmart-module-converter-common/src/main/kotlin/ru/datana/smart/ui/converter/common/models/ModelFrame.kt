package ru.datana.smart.ui.converter.common.models

import java.time.Instant

data class ModelFrame(
    var frameId: String = "",
    var frameTime: Instant = Instant.MIN,
    var framePath: String = "",
    var image: String = "",
    var channel: Channels = Channels.NONE,
    var buffer: ByteArray = ByteArray(0)
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
