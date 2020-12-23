package ru.datana.smart.ui.converter.common.models

data class ModelSignaler(
    var level: ModelSignalerLevel = ModelSignalerLevel.NONE,
    var sound: ModelSignalerSound = ModelSignalerSound.NONE
) {
    companion object {
        val NONE = ModelSignaler()
    }

    enum class ModelSignalerLevel {
        NONE,
        NO_SIGNAL,
        INFO,
        WARNING,
        CRITICAL
    }
}
