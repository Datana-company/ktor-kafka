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

            konveyor {
                on {
                    avgSteelRate.isNotEmpty() && streamRateCriticalPoint.isNotEmpty()
                        && avgSteelRate.toPercent() > streamRateCriticalPoint.toPercent()
                }
                +AddWarningEventToHistoryHandler
                +AddStatelessWarningEventToHistoryHandler
//                +AddStatelessInfoEventToHistoryHandler
                +AddCriticalEventToHistoryHandler
                +CreateCriticalEventHandler
            }
            konveyor {
                on {
                    avgSteelRate.isNotEmpty() && streamRateCriticalPoint.isNotEmpty()
                        && avgSteelRate.toPercent() > streamRateWarningPoint.toPercent()
                        && avgSteelRate.toPercent() <= streamRateCriticalPoint.toPercent()
                }
                +AddCriticalEventToHistoryHandler
                +AddStatelessCriticalEventToHistoryHandler
//                +AddStatelessInfoEventToHistoryHandler
                +AddWarningEventToHistoryHandler
                +CreateWarningEventHandler
            }
//            konveyor {
//                on {
//                    avgSteelRate.isNotEmpty() && streamRateWarningPoint.isNotEmpty()
//                        && avgSteelRate.toPercent() == streamRateWarningPoint.toPercent()
//                }
//                +AddCriticalEventToHistoryHandler
//                +AddStatelessCriticalEventToHistoryHandler
//                +AddWarningEventToHistoryHandler
//                +AddStatelessWarningEventToHistoryHandler
//                +AddInfoEventToHistoryHandler
//                +CreateInfoEventHandler
//            }
            konveyor {
                on {
                    avgSteelRate.isNotEmpty() && streamRateWarningPoint.isNotEmpty()
                        && avgSteelRate.toPercent() <= streamRateWarningPoint.toPercent()
                }
                +AddCriticalEventToHistoryHandler
                +AddStatelessCriticalEventToHistoryHandler
                +AddWarningEventToHistoryHandler
                +AddStatelessWarningEventToHistoryHandler
//                +AddStatelessInfoEventToHistoryHandler
            }
            konveyor {
                on { currentMeltId.isEmpty() }
                +AddStatelessCriticalEventToHistoryHandler
                +AddStatelessWarningEventToHistoryHandler
//                +AddStatelessInfoEventToHistoryHandler
                +CreateSuccessMeltEventHandler
            }
            konveyor {
                on { extEvent.alertRuleId.isNotBlank() }
                +CreateExtEventHandler
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
