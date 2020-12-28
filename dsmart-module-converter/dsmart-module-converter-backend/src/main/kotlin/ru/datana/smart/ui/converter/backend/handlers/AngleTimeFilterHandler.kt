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
        val newAngleTime = context.currentStateRepository.compareAndUpdateLastTimeAngles(null, angleTime)
//        val newAngleTime = context.lastTimeAngles.updateAndGet {
//            maxOf(angleTime, it)
//        }

        if (newAngleTime != angleTime) {
            context.status = CorStatus.FINISHED
        }

    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
