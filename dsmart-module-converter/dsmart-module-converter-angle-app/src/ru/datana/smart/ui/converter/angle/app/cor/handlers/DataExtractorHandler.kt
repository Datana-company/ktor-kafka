package ru.datana.smart.ui.converter.angle.app.cor.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.angle.app.cor.context.ConverterAngleContext
import ru.datana.smart.ui.converter.angle.app.cor.context.CorError
import ru.datana.smart.ui.converter.angle.app.cor.context.CorStatus
import java.nio.file.Files

object DataExtractorHandler: IKonveyorHandler<ConverterAngleContext<String, String>> {

    override suspend fun exec(context: ConverterAngleContext<String, String>, env: IKonveyorEnvironment) {
        if (Files.exists(context.anglesFilePath) && !Files.isDirectory(context.anglesFilePath)) {
            val msg = "No file in path ${context.anglesFilePath}"
            context.logger.error(msg)
            context.errors.add(CorError(msg))
            context.status = CorStatus.FAILING
        }


    }

    override fun match(context: ConverterAngleContext<String, String>, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
