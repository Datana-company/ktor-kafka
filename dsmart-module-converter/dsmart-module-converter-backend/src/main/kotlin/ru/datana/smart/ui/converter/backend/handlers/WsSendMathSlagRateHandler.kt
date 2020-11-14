package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.*

object WsSendMathSlagRateHandler: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        context.wsManager.sendSlagRate(context)

        val schedule = context.scheduleCleaner.get()
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

                val curState = context.currentState.get()
                curState.lastSlagRate = context.slagRate

                context.wsManager.sendSlagRate(context)
                println("jobMath done")
            }
        }

        val curState = context.currentState.get()
        curState.lastSlagRate = context.slagRate
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
