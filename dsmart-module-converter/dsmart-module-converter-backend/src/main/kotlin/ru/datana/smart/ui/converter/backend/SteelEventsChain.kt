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
            +CalcStreamStatus

            konveyor {
                on { streamStatus == ModelStreamStatus.CRITICAL }
                +AddEventToHistoryHandler
                +AddStatelessEventToHistoryHandler
                +CreateCriticalSteelEventHandler
            }
            konveyor {
                on { streamStatus == ModelStreamStatus.WARNING }
                +AddEventToHistoryHandler
                +AddStatelessEventToHistoryHandler
                +CreateWarningSteelEventHandler
            }
//            konveyor {
//                on { streamStatus == ModelStreamStatus.INFO }
//                +AddEventToHistoryHandler
//                +AddStatelessEventToHistoryHandler
//                +CreateInfoSteelEventHandler
//            }
            konveyor {
                on { streamStatus == ModelStreamStatus.NORMAL }
                +AddEventToHistoryHandler
                +AddStatelessEventToHistoryHandler
            }
            konveyor {
                on { currentMeltId.isEmpty() }
                +AddStatelessEventToHistoryHandler
                +CreateSuccessMeltSteelEventHandler
            }
            konveyor {
                on { extEvent.alertRuleId.isNotBlank() }
                +CreateExtEventHandler
            }
            handler {
                onEnv { status == CorStatus.STARTED }
                exec {
                    events = eventsRepository.getAllByMeltId(meltInfo.id)
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
