package ru.datana.smart.ui.converter.backend

import codes.spectrum.konveyor.DefaultKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.konveyor
import ru.datana.smart.ui.converter.backend.common.ConverterChainSettings
import ru.datana.smart.ui.converter.backend.common.setSettings
import ru.datana.smart.ui.converter.backend.handlers.*
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.*

class SlagEventsChain(
    var chainSettings: ConverterChainSettings
) {

    suspend fun exec(context: ConverterBeContext) {
        exec(context, DefaultKonveyorEnvironment)
    }

    suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        context.setSettings(chainSettings)
        konveyor.exec(context, env)
    }

    companion object {
        val konveyor = konveyor<ConverterBeContext> {

            +GetActiveEventHandler
            +SetStreamStatus

            konveyor {
                on { streamStatus == ModelStreamStatus.CRITICAL }
                +SetEventExecutionStatusHandler
                +SetEventInactiveStatusHandler
                +UpdateEventHandler
                +CreateCriticalSlagEventHandler
            }
            konveyor {
                on { streamStatus == ModelStreamStatus.WARNING }
                +SetEventExecutionStatusHandler
                +SetEventInactiveStatusHandler
                +UpdateEventHandler
                +CreateWarningSlagEventHandler
            }
            konveyor {
                on { streamStatus == ModelStreamStatus.INFO }
                +SetEventExecutionStatusHandler
                +SetEventInactiveStatusHandler
                +UpdateEventHandler
                +CreateInfoSlagEventHandler
            }
            konveyor {
                on { streamStatus == ModelStreamStatus.NORMAL }
                +SetEventExecutionStatusHandler
                +SetEventInactiveStatusHandler
                +UpdateEventHandler
            }
            konveyor {
                on { meltInfo.id.isEmpty() }
                +SetEventInactiveStatusHandler
                +UpdateEventHandler
                +CreateSuccessMeltSlagEventHandler
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
        }
    }
}
