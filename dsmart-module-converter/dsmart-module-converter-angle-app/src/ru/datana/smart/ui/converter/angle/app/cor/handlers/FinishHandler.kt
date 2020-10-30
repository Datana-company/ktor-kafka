package ru.datana.smart.ui.converter.angle.app.cor.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.logger.datanaLogger
import ru.datana.smart.ui.converter.angle.app.cor.context.ConverterAngleContext
import ru.datana.smart.ui.converter.angle.app.cor.context.CorStatus
import java.time.Instant

object FinishHandler : IKonveyorHandler<ConverterAngleContext<String, String>> {

    private val logger = datanaLogger(FinishHandler::class.java)

    override suspend fun exec(context: ConverterAngleContext<String, String>, env: IKonveyorEnvironment) {
        context.status = if (context.errors.isEmpty()) CorStatus.SUCCESS else CorStatus.ERROR
        context.timeStop = Instant.now()

        logger.trace(
            "Conveyor has ended. Started: {}. Finished: {}",
            objs = *arrayOf(
                context.timeStart,
                context.timeStop
            )
        )
    }

    override fun match(context: ConverterAngleContext<String, String>, env: IKonveyorEnvironment): Boolean {
        return true
    }

}
