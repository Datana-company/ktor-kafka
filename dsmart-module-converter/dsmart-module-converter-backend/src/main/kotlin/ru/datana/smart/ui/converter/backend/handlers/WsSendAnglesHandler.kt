package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.*

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
                context.angles = ModelAngles.NONE
                context.wsManager.sendAngles(context)

                val curState = context.currentState.get() ?: CurrentState()
                with(curState.lastSlagRate) {
                    if ((slagRate == Double.MIN_VALUE || slagRate == 0.0) &&
                        (steelRate == Double.MIN_VALUE || steelRate == 0.0) &&
                        context.meltInfo.id == curState.currentMeltInfo.id) {
                        curState.currentMeltInfo = ModelMeltInfo.NONE
                        context.meltInfo = ModelMeltInfo.NONE
                        context.wsManager.sendInit(context)
                        context.frame = ModelFrame(channel = ModelFrame.Channels.CAMERA)
                        context.wsManager.sendFrames(context)
                        context.frame = ModelFrame(channel = ModelFrame.Channels.MATH)
                        context.wsManager.sendFrames(context)
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
