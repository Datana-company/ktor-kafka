package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.ModelEvent

/*
* CreateExternalEventHandler - создаётся внешнее событие.
* */
object CreateExternalEventHandler : IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        if (context.activeEvent != ModelEvent.NONE) {
            return
        }

        val meltId: String = context.currentMeltId
        val timeStart = context.timeStart
        context.activeEvent = ModelEvent(
            meltId = meltId,
            type = ModelEvent.EventType.EXTERNAL_EVENT,
            timeStart = timeStart,
            timeFinish = timeStart,
            title = "Информация",
            textMessage = context.externalEvent.textMessage,
            alertRuleId = context.externalEvent.alertRuleId,
            containerId = context.externalEvent.containerId,
            component = context.externalEvent.component,
            timestamp = context.externalEvent.timestamp,
            level = context.externalEvent.level,
            loggerName = context.externalEvent.loggerName,
            category = ModelEvent.Category.INFO
        )
        context.eventsRepository.create(context.activeEvent)
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
