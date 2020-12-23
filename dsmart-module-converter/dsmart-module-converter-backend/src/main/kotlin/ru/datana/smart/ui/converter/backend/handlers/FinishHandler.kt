package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import java.time.Instant

/*
* FinishHandler - обработчик завершения цепочек.
* */
object FinishHandler : IKonveyorHandler<ConverterBeContext> {

    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        if (context.errors.isEmpty()) {
            context.status = CorStatus.SUCCESS
        }
        else {
            context.status = CorStatus.ERROR
        }
        context.timeStop = Instant.now()
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return true
    }

}
