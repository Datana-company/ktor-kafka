package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus


/*
* CalcAvgSlagRateHandler - вычисляем усредненное значение содержания шлака.
* */
object CalcAvgSlagRateHandler: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        val roundingWeight = context.roundingWeight
        with(context.slagRate) {
            context.currentState.get().lastSlagRate.avgSlagRate.takeIf { it != Double.MIN_VALUE }?.let {
                avgSlagRate = slagRate * roundingWeight + it * (1 - roundingWeight)
//                avgSlagRate = it + (slagRate - it) * roundingWeight
            } ?: run {
                avgSlagRate = slagRate
            }
        }
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
