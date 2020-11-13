package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.datana.smart.ui.converter.backend.EventsChain
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.CurrentState
import ru.datana.smart.ui.converter.common.models.ModelEvents
import ru.datana.smart.ui.converter.common.models.ModelMeltInfo
import ru.datana.smart.ui.converter.common.models.ScheduleCleaner

object WsSendMeltFinishHandler: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        val schedule = context.scheduleCleaner.get() ?: ScheduleCleaner()
        with(schedule) {
            jobMeltFinish?.let {
                if (it.isActive) {
                    it.cancel()
                    println("cancel jobMeltFinish")
                }
            }
            jobMeltFinish = GlobalScope.launch {
                delay(10000L)
                val curState = context.currentState.get() ?: CurrentState()
                curState.currentMeltInfo = ModelMeltInfo.NONE
                context.currentState.set(curState)

                EventsChain.konveyor.exec(context)

                val events = context.eventsRepository.getAllByMeltId(context.meltInfo.id)
                context.events = ModelEvents(events = events)
                context.meltInfo = ModelMeltInfo.NONE
                context.wsManager.sendFinish(context)
                context.wsManager.sendEvents(context)
                println("jobMeltFinish done")
            }
        }
        context.scheduleCleaner.set(schedule)
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
