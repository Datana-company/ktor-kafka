package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.extensions.eventSteelCriticalReached
import ru.datana.smart.ui.converter.common.models.ModelEvent

/*
* CreateCriticalSteelEventHandler - создаётся событие типа "Критическая ситуация" по содержанию металла.
* */
object CreateCriticalSteelEventHandler : IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        if (context.activeEvent != ModelEvent.NONE) {
            return
        }

        context.eventSteelCriticalReached()
        context.eventRepository.create(context.activeEvent)
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
