package ru.datana.smart.ui.converter.angle.app.cor.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.logger.datanaLogger
import ru.datana.smart.ui.converter.angle.app.cor.context.ConverterAngleContext
import ru.datana.smart.ui.converter.angle.app.cor.context.CorError
import ru.datana.smart.ui.converter.angle.app.cor.context.CorStatus
import ru.datana.smart.ui.converter.angle.app.models.AngleSchedule
import java.io.File

object ScheduleExtractorHandler : IKonveyorHandler<ConverterAngleContext<String, String>> {

    private val logger = datanaLogger(ScheduleExtractorHandler::class.java)

    override suspend fun exec(context: ConverterAngleContext<String, String>, env: IKonveyorEnvironment) {
        try {
            val scheduleAbsolutePath = "${context.scheduleBasePath}/${context.scheduleRelativePath}"
            logger.trace(
                "Reading file: {}",
                objs = arrayOf(
                    scheduleAbsolutePath
                )
            )

            val json = File(scheduleAbsolutePath).readText(Charsets.UTF_8)
//            val json = File("resources/schedule.json").readText(Charsets.UTF_8)

            context.angleSchedule = context.jacksonSerializer.readValue(
                json,
                AngleSchedule::class.java
            )
        } catch (e: Throwable) {
            val msg = e.message ?: ""
            logger.error(msg)
            context.errors.add(CorError(msg))
            context.status = CorStatus.FAILING
        }
    }

    override fun match(context: ConverterAngleContext<String, String>, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
