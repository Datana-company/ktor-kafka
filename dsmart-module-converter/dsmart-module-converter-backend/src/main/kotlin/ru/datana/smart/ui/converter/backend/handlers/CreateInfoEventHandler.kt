package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext

object CreateInfoEventHandler: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        TODO("Not yet implemented")
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        TODO("Not yet implemented")
    }
}
