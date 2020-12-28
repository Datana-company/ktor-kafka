package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.extensions.eventSlagSuccessReached
import ru.datana.smart.ui.converter.common.models.ModelEvent
import ru.datana.smart.ui.converter.common.utils.toPercent

/*
* CreateSuccessMeltSlagEventHandler - создаётся событие типа "Информация" по содержанию шлака
* об успешном завершении плавки и сразу записывается в историю.
* */
object CreateSuccessMeltSlagEventHandler : IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        if (context.activeEvent != ModelEvent.NONE) {
            return
        }

        val meltId: String = context.currentStateRepository.currentMeltId(null)
        context.eventsRepository.getAllByMeltId(meltId).map {
            if (it.type == ModelEvent.EventType.STREAM_RATE_CRITICAL_EVENT ||
                it.type == ModelEvent.EventType.STREAM_RATE_WARNING_EVENT
            ) {
                return
            }
        }
        context.eventSlagSuccessReached()
        context.eventsRepository.create(context.activeEvent)
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
