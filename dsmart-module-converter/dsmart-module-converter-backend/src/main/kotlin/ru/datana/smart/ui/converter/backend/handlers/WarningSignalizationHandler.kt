package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.ModelSignaler
import ru.datana.smart.ui.converter.common.models.ModelSignalerSound

/*
* WarningSignalizationHandler - светофор переходит в статус "Предупреждение" (лампочка становится жёлтой).
* */
object WarningSignalizationHandler: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        context.signaler = ModelSignaler(
            level = ModelSignaler.ModelSignalerLevel.WARNING,
            sound = ModelSignalerSound.NONE
        )
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
