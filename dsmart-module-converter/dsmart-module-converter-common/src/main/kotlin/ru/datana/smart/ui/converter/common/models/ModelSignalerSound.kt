package ru.datana.smart.ui.converter.common.models

data class ModelSignalerSound(
    var type: ModelSignalerSoundType = ModelSignalerSoundType.NONE,
    var interval: Int = Int.MIN_VALUE
) {
    companion object {
        val NONE = ModelSignalerSound()
    }

    enum class ModelSignalerSoundType {
        NONE,
        SOUND_1,
        SOUND_2,
        SOUND_3
    }
}
