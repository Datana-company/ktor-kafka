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
import ru.datana.smart.ui.converter.common.utils.isNotEmpty
import ru.datana.smart.ui.converter.common.utils.toPercent

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
            +CalcStreamStatus

            konveyor {
                on { streamStatus == ModelStreamStatus.CRITICAL }
                +AddEventToHistoryHandler
                +AddStatelessEventToHistoryHandler
                +CreateCriticalSlagEventHandler
            }
            konveyor {
                on { streamStatus == ModelStreamStatus.WARNING }
                +AddEventToHistoryHandler
                +AddStatelessEventToHistoryHandler
                +CreateWarningSlagEventHandler
            }
//            konveyor {
//                on { streamStatus == ModelStreamStatus.INFO }
//                +AddEventToHistoryHandler
//                +AddStatelessEventToHistoryHandler
//                +CreateInfoSlagEventHandler
//            }
            konveyor {
                on { streamStatus == ModelStreamStatus.NORMAL }
                +AddEventToHistoryHandler
                +AddStatelessEventToHistoryHandler
            }
            konveyor {
                on { currentMeltId.isEmpty() }
                +AddStatelessEventToHistoryHandler
                +CreateSuccessMeltSlagEventHandler
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
