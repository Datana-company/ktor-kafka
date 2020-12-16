package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.ModelEvent
import java.time.Instant
import java.util.*

object CreateExtEventHandler : IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        val meltId: String = context.meltInfo.id
        println(" --- message: " + context.extEvent.textMessage + " --- meltId: " + meltId)
        context.eventsRepository.create(
            ModelEvent(
                meltId = meltId,
                type = ModelEvent.EventType.EXT_EVENT,
                timeStart = Instant.now(),
                timeFinish = Instant.now(),
                textMessage = context.extEvent.textMessage,
                category = when (context.extEvent.level) {
                    "INFO" -> {
                        ModelEvent.Category.INFO
                    }
                    "WARNING" -> {
                        ModelEvent.Category.WARNING
                    }
                    "CRITICAL" -> {
                        ModelEvent.Category.CRITICAL
                    }
                    else -> {
                        ModelEvent.Category.INFO
                    }
                },
                executionStatus = ModelEvent.ExecutionStatus.STATELESS
            )
        )
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
