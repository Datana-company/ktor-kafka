package ru.datana.smart.ui.temperature.app.cor.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import io.ktor.http.cio.websocket.send
import ru.datana.smart.ui.ml.models.TemperatureProcUiDto
import ru.datana.smart.ui.temperature.app.cor.context.CorError
import ru.datana.smart.ui.temperature.app.cor.context.CorStatus
import ru.datana.smart.ui.temperature.app.cor.context.TemperatureBeContext
import ru.datana.smart.ui.temperature.ws.models.WsDsmartResponseTemperature
import ru.datana.smart.ui.temperature.app.mappings.toWsTemperatureModel
import ru.datana.smart.ui.temperature.ws.models.WsDsmartResponseAnalysis
import kotlin.math.max

object FinishHandler : IKonveyorHandler<TemperatureBeContext<String, String>> {

    override suspend fun exec(context: TemperatureBeContext<String, String>, env: IKonveyorEnvironment) {
        if (context.errors.isEmpty()) {
            context.status = CorStatus.SUCCESS
        }
        else context.status = CorStatus.ERROR
    }

    override fun match(context: TemperatureBeContext<String, String>, env: IKonveyorEnvironment): Boolean {
        return true
    }

}
