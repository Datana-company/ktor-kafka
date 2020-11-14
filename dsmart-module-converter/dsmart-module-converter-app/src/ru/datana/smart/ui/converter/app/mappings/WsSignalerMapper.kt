package ru.datana.smart.ui.converter.app.mappings

import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.models.SignalerModel
import ru.datana.smart.ui.converter.common.models.SignalerSoundModel
import ru.datana.smart.ui.converter.ws.models.WsDsmartConverterSignaler
import ru.datana.smart.ui.converter.ws.models.WsDsmartConverterSignalerSound

fun toWsConverterSignalerModel(signalerModel: SignalerModel) =
    WsDsmartConverterSignaler(
        level = signalerModel.level.takeIf { it != SignalerModel.SignalerLevelModel.NONE }
            ?.let { WsDsmartConverterSignaler.SignalerLevelModel.valueOf(it.name) },
        sound = WsDsmartConverterSignalerSound(
            type = signalerModel.sound.type.takeIf { it != SignalerSoundModel.SignalerSoundTypeModel.NONE }
                ?.let { WsDsmartConverterSignalerSound.SignalerSoundTypeModel.valueOf(it.name) },
            interval = signalerModel.sound.interval.takeIf { it != Int.MIN_VALUE }
        )
    )
