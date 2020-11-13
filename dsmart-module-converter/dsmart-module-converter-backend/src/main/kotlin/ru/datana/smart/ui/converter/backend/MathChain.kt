package ru.datana.smart.ui.converter.backend

import codes.spectrum.konveyor.DefaultKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.konveyor
import ru.datana.smart.ui.converter.backend.handlers.*
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.*
import ru.datana.smart.ui.converter.common.repositories.IUserEventsRepository
import java.util.concurrent.atomic.AtomicReference

class MathChain(
    var eventsRepository: IUserEventsRepository,
    var wsManager: IWsManager,
    var dataTimeout: Long,
    var metalRateCriticalPoint: Double,
    var metalRateWarningPoint: Double,
    var timeReaction: Long,
    var timeLimitSiren: Long,
    var currentState: AtomicReference<CurrentState?>,
    var scheduleCleaner: AtomicReference<ScheduleCleaner?>,
    var converterId: String,
    var framesBasePath: String
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
                it.timeReaction = timeReaction
                it.timeLimitSiren = timeLimitSiren
                it.currentState = currentState
                it.scheduleCleaner = scheduleCleaner
                it.converterId = converterId
                it.framesBasePath = framesBasePath
            },
            env
        )
    }

    companion object {
        val konveyor = konveyor<ConverterBeContext> {

            +DevicesFilterHandler
            +MeltFilterHandler
            +FrameTimeFilterHandler
            +EncodeBase64Handler
            +WsSendMathFrameHandler
            konveyor {
                // Временный фильтр на выбросы матмодели по содержанию металла из-за капель металла
                // в начале и в конце слива
                on { slagRate.steelRate <= 0.35 }

                +WsSendMathSlagRateHandler

                handler {
                    onEnv { status == CorStatus.STARTED }
                    exec {
                        EventsChain(
                            eventsRepository = eventsRepository,
                            wsManager = wsManager,
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
            }
//            +WsSendMeltFinishHandler

            +FinishHandler
        }
    }
}
