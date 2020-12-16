package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.ModelEvent
import ru.datana.smart.ui.converter.common.models.ModelSignaler
import ru.datana.smart.ui.converter.common.models.ModelSignalerSound

object CriticalSignalizationHandler: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        context.signaler = ModelSignaler(
            level = ModelSignaler.ModelSignalerLevel.CRITICAL,
            sound = ModelSignalerSound(
                ModelSignalerSound.ModelSignalerSoundType.SOUND_1, 3000
            )
        )
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
