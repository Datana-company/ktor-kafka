package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.events.MetalRateCriticalEvent
import ru.datana.smart.ui.converter.common.events.MetalRateWarningEvent
import ru.datana.smart.ui.converter.common.events.SuccessMeltEvent
import java.util.*

object CreateSuccessMeltEventHandler: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        println("Success Event Start")
        val meltId: String = context.currentState.get()?.currentMeltInfo?.id ?: return
        val currentMeltId: String = context.meltInfo.id
        val slagRateTime = context.frame.frameTime
        context.eventsRepository.getAllByMeltId(meltId).map {
            if (it is MetalRateCriticalEvent || it is MetalRateWarningEvent) {
                println("Success Event Return")
                return
            }
        }
        context.eventsRepository.put(
            currentMeltId,
            SuccessMeltEvent(
                id = UUID.randomUUID().toString(),
                timeStart = slagRateTime,
                timeFinish = slagRateTime,
                warningPoint = context.metalRateWarningPoint,
                isActive = false
            )
        )
        println("Success Event Finish")
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
