package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus

/*
* AngleTimeFilterHandler - происходит фильтрация углов по времени.
* Если текущий угол уже неактулен по времени, то дальше chain не занимается его обработкой.
* */
object AngleTimeFilterHandler: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        val angleTime = context.angles.angleTime
        val lastTime = context.currentStateRepository.lastTimeAngles(context.converterId)

        if (lastTime > angleTime) {
            context.status = CorStatus.FINISHED
        } else {
            context.currentStateRepository.updateLastTimeAngles(context.converterId, angleTime)
        }

    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
