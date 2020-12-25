package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.extensions.eventMetalInfoReached
import ru.datana.smart.ui.converter.common.models.ModelEvent
import ru.datana.smart.ui.converter.common.utils.toPercent

/*
* CreateInfoSteelEventHandler - создаётся событие типа "Информация" по содержанию металла.
* */
object CreateInfoSteelEventHandler : IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        if (context.activeEvent != ModelEvent.NONE) {
            return
        }

        context.activeEvent = context.eventMetalInfoReached()
        context.eventsRepository.create(context.activeEvent)
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
