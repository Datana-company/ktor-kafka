package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.datana.smart.ui.converter.backend.SlagEventsChain
import ru.datana.smart.ui.converter.backend.SteelEventsChain
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.events.EventMode
import ru.datana.smart.ui.converter.common.models.ModelMeltInfo

object WsSendMeltFinishHandler: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        val schedule = context.scheduleCleaner.get()
        with(schedule) {
            jobMeltFinish?.let {
                if (it.isActive) {
                    it.cancel()
                    println("cancel jobMeltFinish")
                }
            }
            jobMeltFinish = GlobalScope.launch {
                delay(context.meltTimeout)
                val curState = context.currentState.get()
                curState.currentMeltInfo = ModelMeltInfo.NONE
                context.status = CorStatus.STARTED

                if (context.eventMode == EventMode.STEEL) {
                    context.converterFacade.handleSteelEvents(context)
                } else if (context.eventMode == EventMode.SLAG) {
                    context.converterFacade.handleSlagEvents(context)
                }

                val events = context.eventsRepository.getAllByMeltId(context.meltInfo.id)
                context.events = events
                context.meltInfo = ModelMeltInfo.NONE
                context.wsManager.sendFinish(context)
                context.wsManager.sendEvents(context)
                context.wsSignalerManager.sendSignaler(context)
                println("jobMeltFinish done")
            }
        }
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
