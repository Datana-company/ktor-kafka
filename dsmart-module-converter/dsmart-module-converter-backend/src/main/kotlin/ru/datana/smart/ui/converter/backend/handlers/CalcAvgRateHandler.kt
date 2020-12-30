package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.ModelSlagRate
import ru.datana.smart.ui.converter.common.utils.calcAvgValue

/*
* CalcAvgRateHandler - вычисляем усредненное значение металла и шлака.
* */
object CalcAvgRateHandler: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        with(context) {
            val lastAvgSteelRate = currentStateRepository.lastAvgSteelRate()
            val lastAvgSlagRate = currentStateRepository.lastAvgSlagRate()
            val currentSteelRate = slagRate.steelRate
            val currentSlagRate = slagRate.slagRate
            // высчитываем усреднённое значение металла
            val avgSteelRate = currentSteelRate.calcAvgValue(lastAvgSteelRate, roundingWeight)
            // высчитываем усреднённое значение шлака
            val avgSlagRate = currentSlagRate.calcAvgValue(lastAvgSlagRate, roundingWeight)
            // сохраняем данные
            currentStateRepository.updateLastAvgSteelRate(avgSteelRate)
            currentStateRepository.updateLastAvgSlagRate(avgSlagRate)
            slagRate = ModelSlagRate(
                slagRateTime = timeStart,
                steelRate = currentSteelRate,
                slagRate = currentSlagRate,
                avgSteelRate = avgSteelRate,
                avgSlagRate = avgSlagRate
            )
        }
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
