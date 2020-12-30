package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.ModelEventMode
import ru.datana.smart.ui.converter.common.models.ModelSlagRate

/*
* CalcAvgSlagRateHandler - вычисляем усредненное значение металла или шлака в зависимости от режима событий.
* */
object CalcAvgSlagRateHandler: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        // в зависимости от режима событий, берётся значение процента содержания шлака или металла
        // (на основе него вычисляется усреднённое значение)
        val currentSlagRate = if (context.eventMode == ModelEventMode.STEEL) {
            context.slagRate.steelRate
        } else {
            context.slagRate.slagRate
        }
        val lastAvgSlagRate = context.currentStateRepository.lastAvgSlagRate(context.converterId)
        val roundingWeight = context.roundingWeight
        // если усреднённое значение ещё не было высчитано,
        // то берётся текущее значение процента содержания
        val avgSlagRate = if (lastAvgSlagRate != Double.MIN_VALUE) {
            currentSlagRate * roundingWeight + lastAvgSlagRate * (1 - roundingWeight)
//            lastAvgSlagRate + (currentSlagRate - lastAvgSlagRate) * roundingWeight
        } else {
            currentSlagRate
        }
        context.currentStateRepository.updateLastAvgSlagRate(context.converterId, avgSlagRate)
        context.slagRate = ModelSlagRate(
            slagRateTime = context.timeStart,
            steelRate = context.slagRate.steelRate,
            slagRate = context.slagRate.slagRate,
            avgSlagRate = avgSlagRate
        )
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
