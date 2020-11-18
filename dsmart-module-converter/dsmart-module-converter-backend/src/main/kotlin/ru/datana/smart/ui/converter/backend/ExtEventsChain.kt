package ru.datana.smart.ui.converter.backend

import codes.spectrum.konveyor.DefaultKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.konveyor
import ru.datana.smart.ui.converter.backend.common.ConverterChainSettings
import ru.datana.smart.ui.converter.backend.common.setSettings
import ru.datana.smart.ui.converter.backend.handlers.FinishHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus

class ExtEventsChain(
    var chainSettings: ConverterChainSettings
) {

    suspend fun exec(context: ConverterBeContext) {
        exec(context, DefaultKonveyorEnvironment)
    }

    suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        context.setSettings(chainSettings)
        konveyor.exec(
            context,
            env
        )
    }

    companion object {
        val konveyor = konveyor<ConverterBeContext> {

            timeout { 1000 }

            // Тут какие-то хэндлеры для обработки внешнего эвента (Какие, пока не понятно)

            handler {
                onEnv { status == CorStatus.STARTED }
                exec {
                    converterFacade.handleEvents(this)
                }
            }
            +FinishHandler
        }
    }
}
