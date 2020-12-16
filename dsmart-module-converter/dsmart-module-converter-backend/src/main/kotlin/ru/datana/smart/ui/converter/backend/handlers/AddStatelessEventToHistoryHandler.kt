package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.ModelEvent
import ru.datana.smart.ui.converter.common.models.ModelStreamStatus

/*
* AddStatelessEventToHistoryHandler - записывает текущее событие в историю без изменения статуса.
* */
object AddStatelessEventToHistoryHandler: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        val actualEventCategory = when (context.streamStatus) {
            ModelStreamStatus.CRITICAL -> ModelEvent.Category.CRITICAL
            ModelStreamStatus.WARNING -> ModelEvent.Category.WARNING
            ModelStreamStatus.INFO -> ModelEvent.Category.INFO
            ModelStreamStatus.NORMAL -> ModelEvent.Category.NONE
            ModelStreamStatus.NONE -> ModelEvent.Category.NONE
        }
        context.activeEvent.takeIf { it.category != actualEventCategory && it != ModelEvent.NONE }?.let {
            it.isActive = false
            context.eventsRepository.update(it)
            context.activeEvent = ModelEvent.NONE
        }
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
