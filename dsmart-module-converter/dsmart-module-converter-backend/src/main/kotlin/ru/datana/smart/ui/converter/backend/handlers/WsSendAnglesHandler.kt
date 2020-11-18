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

        val schedule = context.scheduleCleaner.get()
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

                val curState = context.currentState.get()
                curState.lastAngles = context.angles

                context.wsManager.sendAngles(context)
                println("jobAngles done")
            }
        }

        val curState = context.currentState.get()
        curState.lastAngles = context.angles
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
