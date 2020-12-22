package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.CurrentState

/*
* SetCurrentMeltInfoHandler - в репозиторий текущего состояния задаётся текущие данные о плавке.
* */
object SetCurrentMeltInfoHandler: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        if (context.meltInfo.id == "") return
        val currentState = CurrentState(
            currentMeltInfo = context.meltInfo
        )
        context.currentState.set(currentState)
        println("added topic = meta, currentMeltId = ${context.currentMeltId}")
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
