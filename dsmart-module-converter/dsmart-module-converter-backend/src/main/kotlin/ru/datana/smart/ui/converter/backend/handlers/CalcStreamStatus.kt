package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.ModelStreamStatus
import ru.datana.smart.ui.converter.common.utils.isNotEmpty
import ru.datana.smart.ui.converter.common.utils.toPercent

object CalcStreamStatus: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        with(context) {
            val curState = currentState.get()
            curState.streamStatus = if (avgStreamRate.isNotEmpty() && streamRateCriticalPoint.isNotEmpty()) {
                if (avgStreamRate.toPercent() > streamRateCriticalPoint.toPercent()) ModelStreamStatus.CRITICAL
                else if (avgStreamRate.toPercent() > streamRateWarningPoint.toPercent()
                    && avgStreamRate.toPercent() <= streamRateCriticalPoint.toPercent()) ModelStreamStatus.WARNING
//                else if (avgStreamRate.toPercent() == streamRateWarningPoint.toPercent()) ModelStreamStatus.INFO
                else if (avgStreamRate.toPercent() <= streamRateWarningPoint.toPercent()) ModelStreamStatus.NORMAL
                else ModelStreamStatus.NONE
            } else {
               ModelStreamStatus.NONE
            }
        }
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
