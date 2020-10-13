package ru.datana.smart.ui.temperature.app.cor.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.temperature.app.cor.context.CorError
import ru.datana.smart.ui.temperature.app.cor.context.CorStatus
import ru.datana.smart.ui.temperature.app.cor.context.TemperatureBeContext
import ru.datana.smart.ui.temperature.ws.models.WsDsmartResponseTemperature
import ru.datana.smart.ui.temperature.ws.models.WsDsmartResponseAnalysis

object JsonSerializerHandler : IKonveyorHandler<TemperatureBeContext<String, String>> {

    override suspend fun exec(context: TemperatureBeContext<String, String>, env: IKonveyorEnvironment) {
        context.forwardJsonObjects = context.forwardObjects.map {
            when(it) {
                is WsDsmartResponseTemperature -> context.kotlinxSerializer.encodeToString(WsDsmartResponseTemperature.serializer(), it)
                is WsDsmartResponseAnalysis -> context.kotlinxSerializer.encodeToString(WsDsmartResponseAnalysis.serializer(), it)
                else -> {
                    val msg = "Unknown type of data"
                    context.logger.trace(msg)
                    context.errors.add(CorError(msg))
                    context.status = CorStatus.FAILING
                    return
                }
            }
        }
    }

    override fun match(context: TemperatureBeContext<String, String>, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
            && context.forwardObjects.isNotEmpty()
    }

}
