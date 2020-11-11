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
import ru.datana.smart.ui.converter.common.models.ModelSlagRate
import ru.datana.smart.ui.converter.common.models.ScheduleCleaner

object WsSendMathHandler: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        context.wsManager.sendSlagRate(context)
        context.wsManager.sendFrames(context)

        val schedule = context.scheduleCleaner.get() ?: ScheduleCleaner()
        with(schedule) {
            jobSlagRate?.let {
                if (it.isActive) {
                    it.cancel()
                    println("cancel jobMath")
                }
            }
            jobSlagRate = GlobalScope.launch {
                delay(context.dataTimeout)
                context.wsManager.sendSlagRate(ConverterBeContext())

                val curState = context.currentState.get() ?: CurrentState()
                with(curState.lastAngles) {
                    if (angle == Double.MIN_VALUE || angle == 0.0) {
                        curState.currentMeltInfo = ModelMeltInfo.NONE
                        println("meltInfo cleared in jobMath")
                    }
                }
                curState.lastSlagRate = ModelSlagRate.NONE
                context.currentState.set(curState)
                println("jobMath done")
            }
            jobFrameMath?.let {
                if (it.isActive) {
                    it.cancel()
                    println("cancel jobFrameMath")
                }
            }
            jobFrameMath = GlobalScope.launch {
                delay(context.dataTimeout)
                context.wsManager.sendFrames(ConverterBeContext())
                println("jobFrameMath done")
            }
        }
        context.scheduleCleaner.set(schedule)

        val curState = context.currentState.get() ?: CurrentState()
        curState.lastSlagRate = context.slagRate
        context.currentState.set(curState)
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
