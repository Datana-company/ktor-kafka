package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorError
import ru.datana.smart.ui.converter.common.models.ModelFrame
import java.io.File
import java.util.*
import kotlin.random.Random


object EncodeBase64Handler : IKonveyorHandler<ConverterBeContext> {

    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        try {
            val bytes = File("${context.framesBasePath}/${context.frame.framePath}").readBytes()

//            Для тестов
//            val folder = if (context.frame.channel == ModelFrame.Channels.CAMERA) "camera" else "math"
//            val bytes = File("resources/images/$folder/${Random.nextInt(0, 12)}.png").readBytes()
////////////////////////////////////////////////////////////////////////////////////////////////////

            context.frame.image = Base64.getEncoder().encodeToString(bytes)
        } catch (e: Throwable) {
            val msg = e.message ?: ""
            context.errors.add(CorError(msg))
            context.status = CorStatus.FAILING
        }
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
