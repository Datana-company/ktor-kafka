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

class EventsChain {

    suspend fun exec(context: ConverterBeContext) {
        exec(context, DefaultKonveyorEnvironment)
    }

    suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        konveyor.exec(context, env)
    }

    companion object {
        val konveyor = konveyor<ConverterBeContext> {

            konveyor {
                on { slagRate.steelRate.takeIf { it != Double.MIN_VALUE }?.let { toPercent(it) > toPercent(metalRateCriticalPoint)  } ?: false }
                +UpdateWarningEventHandler
                +UpdateInfoEventHandler
                +UpdateEndEventHandler
                +CreateCriticalEventHandler
            }
            konveyor {
                on { slagRate.steelRate.takeIf { it != Double.MIN_VALUE }?.let { toPercent(it) > toPercent(metalRateWarningPoint) && toPercent(it) <= toPercent(metalRateCriticalPoint) } ?: false }
                +UpdateCriticalEventHandler
                +UpdateInfoEventHandler
                +UpdateEndEventHandler
                +CreateWarningEventHandler
            }
            konveyor {
                on { slagRate.steelRate.takeIf { it != Double.MIN_VALUE }?.let { toPercent(it) == toPercent(metalRateWarningPoint) } ?: false }
                +UpdateCriticalEventHandler
                +UpdateWarningEventHandler
                +UpdateEndEventHandler
                +CreateInfoEventHandler
            }
            konveyor {
                on { slagRate.steelRate.takeIf { it != Double.MIN_VALUE }?.let { toPercent(it) == 0 } ?: false
                    && slagRate.slagRate.takeIf { it != Double.MIN_VALUE }?.let { toPercent(it) == 0 } ?: false }
                +UpdateCriticalEventHandler
                +UpdateWarningEventHandler
                +UpdateInfoEventHandler
                +CreateSuccessMeltEventHandler
                +CreateEndEventHandler
            }
            konveyor {
                on { angles.angle.takeIf { it != Double.MIN_VALUE } != null }
                +UpdateAngleCriticalEventHandler
                +UpdateAngleWarningEventHandler
                +UpdateAngleInfoEventHandler
            }
            handler {
                onEnv { status == CorStatus.STARTED && currentState.get() != null }
                exec {
                    val currentMeltInfoId = currentState.get()!!.currentMeltInfo.id
                    events = ModelEvents(events = eventsRepository.getAllByMeltId(currentMeltInfoId))
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
