package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.ModelSlagRate

/*
* CalcAvgSlagRateHandler - вычисляем усредненное значение содержания шлака.
* */
object CalcAvgSlagRateHandler: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        val roundingWeight = context.roundingWeight
        val currentSlagRate = context.slagRate.slagRate
        val curState = context.currentState.get()
        val lastAvgSlagRate = curState.avgSlagRate.slagRate
        val avgSlagRate = if (lastAvgSlagRate != Double.MIN_VALUE) {
            currentSlagRate * roundingWeight + lastAvgSlagRate * (1 - roundingWeight)
//            lastAvgSlagRate + (currentSlagRate - lastAvgSlagRate) * roundingWeight
        } else {
            currentSlagRate
        }
        curState.avgSlagRate = ModelSlagRate(
            slagRate = avgSlagRate
        )
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
