package ru.datana.smart.ui.temperature.app.cor.services

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.konveyor
import ru.datana.smart.ui.temperature.app.cor.context.TemperatureBeContext
import ru.datana.smart.ui.temperature.app.cor.handlers.AnalysisTopicHandler
import ru.datana.smart.ui.temperature.app.cor.handlers.RawTopicHandler

class ForwardServiceKafkaUi {

    suspend fun exec(context: TemperatureBeContext<String, String>, env: IKonveyorEnvironment) {
        konveyor.exec(context, env)
    }

    companion object {
        val konveyor = konveyor<TemperatureBeContext<String, String>> {

            timeout { 1000 }

            + RawTopicHandler
            + AnalysisTopicHandler
        }
    }
}
