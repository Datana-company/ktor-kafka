package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.ModelSlagRate


/*
* CalcAvgSteelRateHandler - вычисляем усредненное значение содержания металла.
* */
object CalcAvgSteelRateHandler: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        val roundingWeight = context.roundingWeight
        val currentSteelRate = context.slagRate.steelRate
        val curState = context.currentState.get()
        val lastAvgSteelRate = curState.avgSlagRate.steelRate
        val avgSteelRate = if (lastAvgSteelRate != Double.MIN_VALUE) {
            currentSteelRate * roundingWeight + lastAvgSteelRate * (1 - roundingWeight)
//            lastAvgSteelRate + (currentSteelRate - lastAvgSteelRate) * roundingWeight
        } else {
            currentSteelRate
        }
        curState.avgSlagRate = ModelSlagRate(
            steelRate = avgSteelRate
        )
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
