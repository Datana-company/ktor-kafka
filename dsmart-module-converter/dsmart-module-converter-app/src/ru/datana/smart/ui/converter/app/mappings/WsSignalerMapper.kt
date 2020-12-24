package ru.datana.smart.ui.converter.app.mappings

import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.models.ModelSignaler
import ru.datana.smart.ui.converter.common.models.ModelSignalerSound
import ru.datana.smart.ui.converter.ws.models.WsDsmartConverterSignaler
import ru.datana.smart.ui.converter.ws.models.WsDsmartConverterSignalerSound
import ru.datana.smart.ui.converter.ws.models.WsDsmartResponseConverterSignaler

fun ConverterBeContext.toWsResponseConverterSignaler() =
    WsDsmartResponseConverterSignaler(
        data = toWsConverterSignalerModel(this)
    )

private fun toWsConverterSignalerModel(context: ConverterBeContext) =
    WsDsmartConverterSignaler(
        level = context.signaler.level.takeIf { it != ModelSignaler.ModelSignalerLevel.NONE }
            ?.let { WsDsmartConverterSignaler.SignalerLevelModel.valueOf(it.name) },
        sound = WsDsmartConverterSignalerSound(
            type = context.signaler.sound.type.takeIf { it != ModelSignalerSound.ModelSignalerSoundType.NONE }
                ?.let { WsDsmartConverterSignalerSound.SignalerSoundTypeModel.valueOf(it.name) },
            interval = context.signaler.sound.interval.takeIf { it != Int.MIN_VALUE },
            timeout = context.sirenLimitTime
        )
    )
