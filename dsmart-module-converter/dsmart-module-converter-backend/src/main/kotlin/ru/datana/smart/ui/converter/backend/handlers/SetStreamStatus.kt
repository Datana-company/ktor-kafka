package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.ModelEventMode
import ru.datana.smart.ui.converter.common.models.ModelMeltInfo
import ru.datana.smart.ui.converter.common.models.ModelStreamStatus
import ru.datana.smart.ui.converter.common.utils.isEmpty
import ru.datana.smart.ui.converter.common.utils.isNotEmpty
import ru.datana.smart.ui.converter.common.utils.toPercent

/*
* SetStreamStatus - задаём значение статуса потока на основе усреднённого значения содержания в потоке (металла или шлака).
* */
object SetStreamStatus: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        with(context) {
            val avgSlagRate = if (eventMode == ModelEventMode.STEEL) {
                currentStateRepository.lastAvgSteelRate()
            } else {
                currentStateRepository.lastAvgSlagRate()
            }

            val currentMeltInfo = currentStateRepository.currentMeltInfo()
            context.streamStatus = if (currentMeltInfo.isNotEmpty() && avgSlagRate.isNotEmpty()
                && streamRateCriticalPoint.isNotEmpty() && streamRateWarningPoint.isNotEmpty()) {
                    if (avgSlagRate.toPercent() > streamRateCriticalPoint.toPercent()) ModelStreamStatus.CRITICAL
                    else if (avgSlagRate.toPercent() > streamRateWarningPoint.toPercent()
                        && avgSlagRate.toPercent() <= streamRateCriticalPoint.toPercent()) ModelStreamStatus.WARNING
//                    else if (avgSlagRate.toPercent() == streamRateWarningPoint.toPercent()) ModelStreamStatus.INFO
                    else if (avgSlagRate.toPercent() <= streamRateWarningPoint.toPercent()) ModelStreamStatus.NORMAL
                    else ModelStreamStatus.NONE
            } else if (currentMeltInfo.isEmpty()) {
                ModelStreamStatus.END
            } else {
               ModelStreamStatus.NONE
            }
        }
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
