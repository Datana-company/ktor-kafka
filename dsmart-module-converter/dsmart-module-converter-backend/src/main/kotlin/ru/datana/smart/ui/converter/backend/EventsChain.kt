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
import ru.datana.smart.ui.converter.common.repositories.IUserEventsRepository
import ru.datana.smart.ui.converter.common.utils.toPercent
import java.util.concurrent.atomic.AtomicReference

class EventsChain(
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
                +AddFailedWarningEventToHistoryHandler
//                +AddInfoEventToHistoryHandler
                +UpdateTimeCriticalEventHandler
                +CreateCriticalEventHandler
                +CheckAngleCriticalEventHandler
            }
            konveyor {
                on { slagRate.avgSteelRate.takeIf { it != Double.MIN_VALUE }?.let { toPercent(it) > toPercent(metalRateWarningPoint) && toPercent(it) <= toPercent(metalRateCriticalPoint) } ?: false }
                +AddFailedCriticalEventToHistoryHandler
//                +AddInfoEventToHistoryHandler
                +UpdateTimeWarningEventHandler
                +CreateWarningEventHandler
                +CheckAngleWarningEventHandler
            }
//            konveyor {
//                on { slagRate.avgSteelRate.takeIf { it != Double.MIN_VALUE }?.let { toPercent(it) == toPercent(metalRateWarningPoint) } ?: false }
//                +AddFailedCriticalEventToHistoryHandler
//                +AddFailedWarningEventToHistoryHandler
//                +UpdateTimeInfoEventHandler
//                +CreateInfoEventHandler
//            }
            konveyor {
                on { slagRate.avgSteelRate.takeIf { it != Double.MIN_VALUE }?.let { toPercent(it) <= toPercent(metalRateWarningPoint) } ?: false }
                +AddCriticalEventToHistoryHandler
                +AddWarningEventToHistoryHandler
//                +AddInfoEventToHistoryHandler
            }
            konveyor {
                on { currentState.get().currentMeltInfo.id.isEmpty() }
                +AddFailedCriticalEventToHistoryHandler
                +AddFailedWarningEventToHistoryHandler
//                +AddInfoEventToHistoryHandler
                +CreateSuccessMeltEventHandler
            }
            handler {
                onEnv { status == CorStatus.STARTED }
                exec {
                    val currentMeltInfoId = currentState.get().currentMeltInfo.id
                    events = ModelEvents(events = eventsRepository.getAllByMeltId(currentMeltInfoId))
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
                onEnv { status == CorStatus.STARTED && signaler != SignalerModel.NONE }
                exec {
                    wsSignalerManager.sendSignaler(this)
                }
            }
        }
    }
}
