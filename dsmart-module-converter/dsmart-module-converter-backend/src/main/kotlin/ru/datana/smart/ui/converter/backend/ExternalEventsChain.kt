package ru.datana.smart.ui.converter.backend

import codes.spectrum.konveyor.DefaultKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.konveyor
import ru.datana.smart.ui.converter.backend.common.ConverterChainSettings
import ru.datana.smart.ui.converter.backend.common.setSettings
import ru.datana.smart.ui.converter.backend.handlers.*
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus

class ExternalEventsChain(
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

            handler {
                onEnv { status == CorStatus.STARTED && currentMeltId.isEmpty() }
                exec {
                    status = CorStatus.FINISHED
                }
            }

            +GetActiveEventHandler
            +SetStreamStatus

            konveyor {
                on { status == CorStatus.STARTED && externalEvent.alertRuleId.isNotBlank() }
                +SetEventExecutionStatusHandler
                +SetEventInactiveStatusHandler
                +UpdateEventHandler
                +CreateExternalEventHandler
            }

            handler {
                onEnv { status == CorStatus.STARTED }
                exec {
                    events = eventsRepository.getAllByMeltId(currentMeltId)
                }
            }

            handler {
                onEnv { status == CorStatus.STARTED }
                exec {
                    wsManager.sendEvents(this)
                }
            }

            +FinishHandler
        }
    }
}
