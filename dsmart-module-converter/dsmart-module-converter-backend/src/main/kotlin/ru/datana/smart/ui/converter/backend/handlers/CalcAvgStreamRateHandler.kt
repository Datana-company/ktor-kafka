package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.ModelEventMode

/*
* CalcAvgStreamRateHandler - вычисляем усредненное значение металла или шлака в зависимости от режима событий.
* */
object CalcAvgStreamRateHandler: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        // в зависимости от режима событий, берётся значение процента содержания шлака или металла
        // (на основе него вычисляется усреднённое значение)
        val currentStreamRate = if (context.eventMode == ModelEventMode.STEEL) {
            context.slagRate.steelRate
        } else {
            context.slagRate.slagRate
        }
        val lastAvgStreamRate = context.currentStateRepository.avgStreamRate(null)
        val roundingWeight = context.roundingWeight
        // если усреднённое значение ещё не было высчитано,
        // то берётся текущее значение процента содержания
        val avgStreamRate = if (lastAvgStreamRate != Double.MIN_VALUE) {
            currentStreamRate * roundingWeight + lastAvgStreamRate * (1 - roundingWeight)
//            lastAvgStreamRate + (currentStreamRate - lastAvgStreamRate) * roundingWeight
        } else {
            currentStreamRate
        }
        context.currentStateRepository.updateStreamRate(null, avgStreamRate)
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
