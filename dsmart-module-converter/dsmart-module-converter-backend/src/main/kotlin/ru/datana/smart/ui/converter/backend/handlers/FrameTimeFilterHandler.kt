package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus

/*
* FrameTimeFilterHandler - происходит фильтрация кадров по времени.
* Если текущий кадр уже неактулен по времени, то дальше chain не занимается его обработкой
* */
object FrameTimeFilterHandler: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        val frameTime = context.frame.frameTime
        val lastTime = context.currentStateRepository.lastTimeFrame()
        if (lastTime > frameTime) {
            context.status = CorStatus.FINISHED
        } else {
            context.currentStateRepository.updateLastTimeFrame(frameTime)
        }

    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
