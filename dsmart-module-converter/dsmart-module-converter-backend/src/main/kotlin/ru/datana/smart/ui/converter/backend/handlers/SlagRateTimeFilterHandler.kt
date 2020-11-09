package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import kotlin.math.max

object SlagRateTimeFilterHandler: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        val slagRateTime = context.slagRate.slagRateTime ?: 0L
        val newSlagRateTime = context.lastTimeSlagRate.updateAndGet {
            max(slagRateTime, it)
        }

        if (newSlagRateTime != slagRateTime) {
            context.status = CorStatus.FINISHED
        }

    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
