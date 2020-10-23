package ru.datana.smart.ui.converter.app.cor.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.app.cor.context.ConverterBeContext
import ru.datana.smart.ui.converter.app.cor.context.CorStatus
import ru.datana.smart.ui.converter.app.cor.services.UserEventGenerator
import ru.datana.smart.ui.converter.app.mappings.toWsEventListModel
import ru.datana.smart.ui.converter.ws.models.WsDsmartEventList
import ru.datana.smart.ui.converter.ws.models.WsDsmartResponseEvents

object EventHandler : IKonveyorHandler<ConverterBeContext<String, String>> {

    override suspend fun exec(context: ConverterBeContext<String, String>, env: IKonveyorEnvironment) {

        if (context.metalRateEventGenerator.isNotActive()) {
            context.metalRateEventGenerator.start()
        }

        UserEventGenerator().exec(context, env)

        val response = WsDsmartResponseEvents(
            data = WsDsmartEventList(
                list = toWsEventListModel(context.eventsRepository.getAll())
            )
        )

        context.forwardObjects.add(response)
    }

    override fun match(context: ConverterBeContext<String, String>, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }

}
