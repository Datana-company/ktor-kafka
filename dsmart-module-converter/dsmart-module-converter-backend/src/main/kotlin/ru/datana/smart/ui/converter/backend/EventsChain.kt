package ru.datana.smart.ui.converter.backend

import codes.spectrum.konveyor.DefaultKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.konveyor
import ru.datana.smart.ui.converter.backend.handlers.*
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.*
import ru.datana.smart.ui.converter.common.repositories.IUserEventsRepository
import ru.datana.smart.ui.converter.common.utils.toPercent
import java.util.concurrent.atomic.AtomicReference

class EventsChain(
    var eventsRepository: IUserEventsRepository,
    var wsManager: IWsManager,
    var dataTimeout: Long,
    var metalRateCriticalPoint: Double,
    var metalRateWarningPoint: Double,
    var reactionTime: Long,
    var sirenLimitTime: Long,
    var currentState: AtomicReference<CurrentState?>,
    var scheduleCleaner: AtomicReference<ScheduleCleaner?>,
    var converterId: String
) {
    suspend fun exec(context: ConverterBeContext) {
        exec(context, DefaultKonveyorEnvironment)
    }

    suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        konveyor.exec(
            context.also {
                it.eventsRepository = eventsRepository
                it.wsManager = wsManager
                it.dataTimeout = dataTimeout
                it.metalRateCriticalPoint = metalRateCriticalPoint
                it.metalRateWarningPoint = metalRateWarningPoint
                it.reactionTime = reactionTime
                it.sirenLimitTime = sirenLimitTime
                it.currentState = currentState
                it.scheduleCleaner = scheduleCleaner
                it.converterId = converterId
            },
            env
        )
    }

    companion object {
        val konveyor = konveyor<ConverterBeContext> {

            exec {
                println("EventsChain ${currentState.get()?.currentMeltInfo?.id}")
            }

            konveyor {
                on { slagRate.steelRate.takeIf { it != Double.MIN_VALUE }?.let { toPercent(it) > toPercent(metalRateCriticalPoint)  } ?: false }
                +UpdateWarningEventHandler
                +UpdateInfoEventHandler
                +CreateCriticalEventHandler
            }
            konveyor {
                on { slagRate.steelRate.takeIf { it != Double.MIN_VALUE }?.let { toPercent(it) > toPercent(metalRateWarningPoint) && toPercent(it) <= toPercent(metalRateCriticalPoint) } ?: false }
                +UpdateCriticalEventHandler
                +UpdateInfoEventHandler
                +CreateWarningEventHandler
            }
            konveyor {
                on { slagRate.steelRate.takeIf { it != Double.MIN_VALUE }?.let { toPercent(it) == toPercent(metalRateWarningPoint) } ?: false }
                +UpdateCriticalEventHandler
                +UpdateWarningEventHandler
                +CreateInfoEventHandler
            }
            konveyor {
                on { currentState.get()?.let { it.currentMeltInfo.id == "" } ?: false }
                +UpdateCriticalEventHandler
                +UpdateWarningEventHandler
                +UpdateInfoEventHandler
                +CreateSuccessMeltEventHandler
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
