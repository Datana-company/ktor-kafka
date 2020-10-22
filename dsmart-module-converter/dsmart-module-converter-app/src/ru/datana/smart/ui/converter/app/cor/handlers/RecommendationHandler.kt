package ru.datana.smart.ui.converter.app.cor.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.app.cor.context.ConverterBeContext
import ru.datana.smart.ui.converter.app.cor.context.CorError
import ru.datana.smart.ui.converter.app.cor.context.CorStatus
import ru.datana.smart.ui.converter.app.cor.services.UserEventGenerator
import ru.datana.smart.ui.converter.app.mappings.toWsRecommendationListModel
import ru.datana.smart.ui.converter.ws.models.WsDsmartRecommendationList
import ru.datana.smart.ui.converter.ws.models.WsDsmartResponseRecommendations
import ru.datana.smart.ui.mlui.models.ConverterTransportMlUi

object RecommendationHandler : IKonveyorHandler<ConverterBeContext<String, String>> {

    override suspend fun exec(context: ConverterBeContext<String, String>, env: IKonveyorEnvironment) {

        UserEventGenerator().exec(context, env)

        val response = WsDsmartResponseRecommendations(
            data = WsDsmartRecommendationList(
                list = toWsRecommendationListModel(context.eventsRepository.getAll())
            )
        )

        context.forwardObjects.add(response)
    }

    override fun match(context: ConverterBeContext<String, String>, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }

}
