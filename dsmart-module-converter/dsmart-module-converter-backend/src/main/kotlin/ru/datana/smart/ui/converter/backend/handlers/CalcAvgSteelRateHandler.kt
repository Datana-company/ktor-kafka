package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus


/*
* CalcAvgSteelRateHandler - вычисляем усредненное значение содержания металла.
* */
object CalcAvgSteelRateHandler: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        val roundingWeight = context.roundingWeight
        with(context.slagRate) {
            context.currentState.get().lastSlagRate.avgSteelRate.takeIf { it != Double.MIN_VALUE }?.let {
                avgSteelRate = steelRate * roundingWeight + it * (1 - roundingWeight)
//                avgSteelRate = it + (steelRate - it) * roundingWeight
            } ?: run {
                avgSteelRate = steelRate
            }
        }
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
