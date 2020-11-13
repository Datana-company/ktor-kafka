package ru.datana.smart.ui.converter.backend

import codes.spectrum.konveyor.DefaultKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.konveyor
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.datana.smart.ui.converter.backend.handlers.*
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.*
import ru.datana.smart.ui.converter.common.repositories.IUserEventsRepository
import java.util.concurrent.atomic.AtomicReference

class AnglesChain(
    var eventsRepository: IUserEventsRepository,
    var wsManager: IWsManager,
    var wsSignalerManager: IWsSignalerManager,
    var dataTimeout: Long,
    var metalRateCriticalPoint: Double,
    var metalRateWarningPoint: Double,
    var timeReaction: Long,
    var timeLimitSiren: Long,
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
                it.wsSignalerManager = wsSignalerManager
                it.dataTimeout = dataTimeout
                it.metalRateCriticalPoint = metalRateCriticalPoint
                it.metalRateWarningPoint = metalRateWarningPoint
                it.timeReaction = timeReaction
                it.timeLimitSiren = timeLimitSiren
                it.currentState = currentState
                it.scheduleCleaner = scheduleCleaner
                it.converterId = converterId
            },
            env
        )
    }

    companion object {
        val konveyor = konveyor<ConverterBeContext> {

            +DevicesFilterHandler
            +MeltFilterHandler
            +AngleTimeFilterHandler

            +WsSendAnglesHandler

            handler {
                onEnv { status == CorStatus.STARTED }
                exec {
                    EventsChain(
                        eventsRepository = eventsRepository,
                        wsManager = wsManager,
                        wsSignalerManager = wsSignalerManager,
                        dataTimeout = dataTimeout,
                        metalRateCriticalPoint = metalRateCriticalPoint,
                        metalRateWarningPoint = metalRateWarningPoint,
                        currentState = currentState,
                        scheduleCleaner = scheduleCleaner,
                        timeReaction = timeReaction,
                        timeLimitSiren = timeLimitSiren,
                        converterId = converterId
                    ).exec(this)
                }
            }

            +FinishHandler
        }
    }
}
