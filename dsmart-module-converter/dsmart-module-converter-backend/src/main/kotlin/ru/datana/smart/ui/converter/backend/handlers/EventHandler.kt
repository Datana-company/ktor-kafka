package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus

object EventHandler : IKonveyorHandler<ConverterBeContext> {

    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {

//        UserEventGenerator().exec(context, env)
//
//        val response = WsDsmartResponseEvents(
//            data = WsDsmartEventList(
//                list = toWsEventListModel(context.eventsRepository.getAll())
//            )
//        )
//
//        context.forwardObjects.add(response)
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }

}
