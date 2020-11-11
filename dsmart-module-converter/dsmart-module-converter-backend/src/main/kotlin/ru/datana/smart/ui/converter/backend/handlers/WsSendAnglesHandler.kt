package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.CurrentState
import ru.datana.smart.ui.converter.common.models.ModelAngles
import ru.datana.smart.ui.converter.common.models.ModelMeltInfo
import ru.datana.smart.ui.converter.common.models.ScheduleCleaner

object WsSendAnglesHandler: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        context.wsManager.sendAngles(context)

        val schedule = context.scheduleCleaner.get() ?: ScheduleCleaner()
        with(schedule) {
            jobAngles?.let {
                if (it.isActive) {
                    it.cancel()
                    println("cancel jobAngles")
                }
            }
            jobAngles = GlobalScope.launch {
                delay(context.dataTimeout)
                context.wsManager.sendAngles(ConverterBeContext())

                val curState = context.currentState.get() ?: CurrentState()
                with(curState.lastSlagRate) {
                    if ((slagRate == Double.MIN_VALUE || slagRate == 0.0) &&
                        (steelRate == Double.MIN_VALUE || steelRate == 0.0)) {
                        curState.currentMeltInfo = ModelMeltInfo.NONE
                        println("meltInfo cleared in jobAngles")
                    }
                }
                curState.lastAngles = ModelAngles.NONE
                context.currentState.set(curState)
                println("jobAngles done")
            }
        }
        context.scheduleCleaner.set(schedule)

        val curState = context.currentState.get() ?: CurrentState()
        curState.lastAngles = context.angles
        context.currentState.set(curState)
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
