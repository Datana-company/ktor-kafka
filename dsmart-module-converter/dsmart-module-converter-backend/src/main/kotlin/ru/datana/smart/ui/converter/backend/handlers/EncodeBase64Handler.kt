package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
//import ru.datana.smart.logger.datanaLogger
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorError
import ru.datana.smart.ui.converter.common.models.ModelFrame
import java.io.File
import java.util.*
import kotlin.random.Random


object EncodeBase64Handler : IKonveyorHandler<ConverterBeContext> {

//    val logger = datanaLogger(EncodeBase64Handler::class.java)

    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {

        val imagePath = "${context.framesBasePath}/${context.frame.framePath}"
        try {
            val bytes = File(imagePath).readBytes()

//            Для тестов
//            val folder = if (context.frame.channel == ModelFrame.Channels.CAMERA) "camera" else "math"
//            val bytes = File("resources/images/$folder/${Random.nextInt(0, 12)}.png").readBytes()
////////////////////////////////////////////////////////////////////////////////////////////////////

            context.frame.image = Base64.getEncoder().encodeToString(bytes)
        } catch (e: Throwable) {
//            logger.error("Файл {} не прочитался", objs = arrayOf(imagePath))
            println("Файл ${imagePath} не прочитался")
            val bytes = EncodeBase64Handler::class.java.getResource("/images/math/0.png").readBytes()
            context.frame.image = Base64.getEncoder().encodeToString(bytes)
        }
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
