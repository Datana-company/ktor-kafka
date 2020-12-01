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

            konveyor {
                on { slagRate.avgSteelRate.takeIf { it != Double.MIN_VALUE }?.let { toPercent(it) > toPercent(metalRateCriticalPoint)  } ?: false }
                +AddWarningEventToHistoryHandler
                +AddStatelessWarningEventToHistoryHandler
//                +AddStatelessInfoEventToHistoryHandler
                +AddCriticalEventToHistoryHandler
                +CreateCriticalEventHandler
            }
            konveyor {
                on { slagRate.avgSteelRate.takeIf { it != Double.MIN_VALUE }?.let { toPercent(it) > toPercent(metalRateWarningPoint) && toPercent(it) <= toPercent(metalRateCriticalPoint) } ?: false }
                +AddCriticalEventToHistoryHandler
                +AddStatelessCriticalEventToHistoryHandler
//                +AddStatelessInfoEventToHistoryHandler
                +AddWarningEventToHistoryHandler
                +CreateWarningEventHandler
            }
//            konveyor {
//                on { slagRate.avgSteelRate.takeIf { it != Double.MIN_VALUE }?.let { toPercent(it) == toPercent(metalRateWarningPoint) } ?: false }
//                +AddCriticalEventToHistoryHandler
//                +AddStatelessCriticalEventToHistoryHandler
//                +AddWarningEventToHistoryHandler
//                +AddStatelessWarningEventToHistoryHandler
//                +AddInfoEventToHistoryHandler
//                +CreateInfoEventHandler
//            }
            konveyor {
                on { slagRate.avgSteelRate.takeIf { it != Double.MIN_VALUE }?.let { toPercent(it) <= toPercent(metalRateWarningPoint) } ?: false }
                +AddCriticalEventToHistoryHandler
                +AddStatelessCriticalEventToHistoryHandler
                +AddWarningEventToHistoryHandler
                +AddStatelessWarningEventToHistoryHandler
//                +AddStatelessInfoEventToHistoryHandler
            }
            konveyor {
                on { currentState.get().currentMeltInfo.id.isEmpty() }
                +AddStatelessCriticalEventToHistoryHandler
                +AddStatelessWarningEventToHistoryHandler
//                +AddStatelessInfoEventToHistoryHandler
                +CreateSuccessMeltEventHandler
            }
            konveyor {
                on { extEvents.alertRuleId != null }
                +CreateExtEventHandler
            }
            handler {
                onEnv { status == CorStatus.STARTED }
                exec {
                    val currentMeltInfoId = currentState.get().currentMeltInfo.id
                    events = eventsRepository.getAllByMeltId(currentMeltInfoId)
                }
            }
            handler {
                onEnv { status == CorStatus.STARTED }
                exec {
                    wsManager.sendEvents(this)
                }
            }
//            Цепочка обработки светофора от событий
            handler {
                onEnv { status == CorStatus.STARTED && signaler.level != SignalerModel.SignalerLevelModel.NONE }
                exec {
                    wsSignalerManager.sendSignaler(this)
                }
            }
        }
    }
}
