package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.CurrentState
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
                context.meltInfo = ModelMeltInfo.NONE
                val curState = context.currentState.get() ?: CurrentState()
                curState.currentMeltInfo = context.meltInfo
                context.currentState.set(curState)
                context.wsManager.sendFinish(context)
                println("jobMeltFinish done")
            }
        }
        context.scheduleCleaner.set(schedule)
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
