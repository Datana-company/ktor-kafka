//package ru.datana.smart.ui.converter.angle.app.cor.handlers
//
//import codes.spectrum.konveyor.IKonveyorEnvironment
//import codes.spectrum.konveyor.IKonveyorHandler
//import ru.datana.smart.logger.datanaLogger
//import ru.datana.smart.ui.converter.angle.app.cor.context.ConverterAngleContext
//import ru.datana.smart.ui.converter.angle.app.cor.context.CorError
//import ru.datana.smart.ui.converter.angle.app.cor.context.CorStatus
//import java.lang.IllegalArgumentException
//
//
//object ValidationHandler : IKonveyorHandler<ConverterAngleContext<String, String>> {
//
//    private val logger = datanaLogger(ValidationHandler::class.java)
//
//    override suspend fun exec(context: ConverterAngleContext<String, String>, env: IKonveyorEnvironment) {
//        try {
//            val timeStart = context.metaInfo?.timeStart
//                ?: throw IllegalArgumentException(
//                    "ConverterMeltInfo does not contain value 'timeStart: ${context.metaInfo}"
//                )
//            if (timeStart <= 0) throw IllegalArgumentException(
//                "Field 'timeStart' in ConverterMeltInfo should be positive: ${context.metaInfo}"
//            )
//            context.scheduleRelativePath = context.metaInfo?.devices?.selsyn?.uri
//                ?: throw IllegalArgumentException(
//                    "ConverterMeltInfo does not contain value 'devices.selsyn.uri': ${context.metaInfo}"
//                )
//        } catch (e: Throwable) {
//            val msg = e.message ?: ""
//            logger.error(msg)
//            context.errors.add(CorError(msg))
//            context.status = CorStatus.FAILING
//        }
//    }
//
//    override fun match(context: ConverterAngleContext<String, String>, env: IKonveyorEnvironment): Boolean {
//        return context.status == CorStatus.STARTED
//    }
//}
