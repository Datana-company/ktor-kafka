package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus

object CurrentMeltInfoHandler: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        context.currentMeltInfo.set(context.meltInfo)
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
