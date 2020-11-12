package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.*

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
                context.slagRate = ModelSlagRate.NONE
                context.wsManager.sendSlagRate(context)
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
                context.frame = ModelFrame(channel = ModelFrame.Channels.MATH)
                context.wsManager.sendFrames(context)
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
