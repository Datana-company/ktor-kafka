package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.ModelEvent

object CreateExtendEventHandler : IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        if (context.activeEvent != ModelEvent.NONE) {
            return
        }

        val meltId: String = context.currentMeltId
        val timeStart = context.timeStart
        context.activeEvent = ModelEvent(
            meltId = meltId,
            type = ModelEvent.EventType.EXT_EVENT,
            timeStart = timeStart,
            timeFinish = timeStart,
            textMessage = context.extendEvent.textMessage,
            alertRuleId = context.extendEvent.alertRuleId,
            containerId = context.extendEvent.containerId,
            component = context.extendEvent.component,
            timestamp = context.extendEvent.timestamp,
            level = context.extendEvent.level,
            loggerName = context.extendEvent.loggerName,
            category = ModelEvent.Category.INFO,
            executionStatus = ModelEvent.ExecutionStatus.STATELESS
        )
        context.eventsRepository.create(context.activeEvent)
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
