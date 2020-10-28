package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.CorError
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.context.ConverterBeContext

object WsSendHandler : IKonveyorHandler<ConverterBeContext> {

    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
//        context.forwardJsonObjects.forEach { data ->
//            val wsSessionsIterator = context.wsManager.wsSessions.iterator()
//            while (wsSessionsIterator.hasNext()) {
//                wsSessionsIterator.next().apply {
//                    try {
//                        send(data)
//                    } catch (e: Throwable) {
//                        val msg = "Session ${hashCode()} is removed due to exception $e"
//                        context.errors.add(CorError(msg))
//                        context.status = CorStatus.FAILING
//                        wsSessionsIterator.remove()
//                    }
//                }
//            }
//        }
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }

}
