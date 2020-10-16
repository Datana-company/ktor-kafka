package ru.datana.smart.ui.converter.app.cor.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.app.cor.context.ConverterBeContext
import ru.datana.smart.ui.converter.app.cor.context.CorStatus
import ru.datana.smart.ui.converter.ws.models.WsDsmartRecommendation
import ru.datana.smart.ui.converter.ws.models.WsDsmartResponseRecommendation
import java.time.Instant

object RecommendationHandler : IKonveyorHandler<ConverterBeContext<String, String>> {

    override suspend fun exec(context: ConverterBeContext<String, String>, env: IKonveyorEnvironment) {
        val response = WsDsmartResponseRecommendation(
            data = WsDsmartRecommendation(
                time = Instant.now().toEpochMilli(),
                title = "Внимание",
                textMessage = "Угол наклона слишком большой"
            )
        )
        if (Math.random() < 0.05) {
            context.forwardObjects.add(response)
        }
    }

    override fun match(context: ConverterBeContext<String, String>, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }

}
