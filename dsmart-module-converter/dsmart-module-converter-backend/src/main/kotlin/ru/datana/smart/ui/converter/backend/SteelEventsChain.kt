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

class SteelEventsChain(
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
                +CreateCriticalSteelEventHandler
            }
            konveyor {
                on { streamStatus == ModelStreamStatus.WARNING }
                +SetEventExecutionStatusHandler
                +SetEventInactiveStatusHandler
                +UpdateEventHandler
                +CreateWarningSteelEventHandler
            }
//            konveyor {
//                on { streamStatus == ModelStreamStatus.INFO }
//                +SetEventExecutionStatusHandler
//                +SetEventInactiveStatusHandler
//                +UpdateEventHandler
//                +CreateInfoSteelEventHandler
//            }
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
                +CreateSuccessMeltSteelEventHandler
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
