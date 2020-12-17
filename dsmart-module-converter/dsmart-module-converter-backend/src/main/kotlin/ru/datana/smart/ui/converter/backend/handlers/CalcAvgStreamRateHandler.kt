package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.ModelEventMode

/*
* CalcAvgStreamRateHandler - вычисляем усредненное значение металла или шлака в зависимости от режима рекомендаций.
* */
object CalcAvgStreamRateHandler: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        val currentStreamRate = if (context.eventMode == ModelEventMode.STEEL) {
            context.slagRate.steelRate
        } else {
            context.slagRate.slagRate
        }
        val lastAvgStreamRate = context.avgStreamRate
        val roundingWeight = context.roundingWeight
        val avgStreamRate = if (lastAvgStreamRate != Double.MIN_VALUE) {
            currentStreamRate * roundingWeight + lastAvgStreamRate * (1 - roundingWeight)
//            lastAvgStreamRate + (currentStreamRate - lastAvgStreamRate) * roundingWeight
        } else {
            currentStreamRate
        }
        context.avgStreamRate = avgStreamRate
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
