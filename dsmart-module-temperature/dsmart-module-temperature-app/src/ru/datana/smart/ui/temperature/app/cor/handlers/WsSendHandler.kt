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

object WsSendHandler : IKonveyorHandler<TemperatureBeContext<String, String>> {

    override suspend fun exec(context: TemperatureBeContext<String, String>, env: IKonveyorEnvironment) {
        context.forwardJsonObjects.forEach { data ->
            context.logger.trace("sending to client: $data")
            val wsSessionsIterator = context.wsManager.wsSessions.iterator()
            while (wsSessionsIterator.hasNext()) {
                wsSessionsIterator.next().apply {
                    try {
                        context.logger.trace("Sending to client ${hashCode()}: $data")
                        send(data)
                    } catch (e: Throwable) {
                        val msg = "Session ${hashCode()} is removed due to exception $e"
                        context.logger.error(msg)
                        context.errors.add(CorError(msg))
                        context.status = CorStatus.FAILING
                        wsSessionsIterator.remove()
                    }
                }
            }
        }
    }

    override fun match(context: TemperatureBeContext<String, String>, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
            && context.forwardJsonObjects.isNotEmpty()
    }

}
