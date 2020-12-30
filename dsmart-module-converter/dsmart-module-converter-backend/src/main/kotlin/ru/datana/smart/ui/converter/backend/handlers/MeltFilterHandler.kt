package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus

/*
* MeltFilterHandler - происходит фильтрация данных о плавке.
* Сравнивается идентификатор текущей плавки с идентификатором
* */
object MeltFilterHandler: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        with (context) {
            val currentMeltId = currentStateRepository.currentMeltId()
            if (currentMeltId != meltInfo.id || currentMeltId.isEmpty()) {
                status = CorStatus.FINISHED
            }
        }
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
