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

class FrameChain(
    var eventsRepository: IUserEventsRepository,
    var wsManager: IWsManager,
    var dataTimeout: Long,
    var metalRateCriticalPoint: Double,
    var metalRateWarningPoint: Double,
    var reactionTime: Long,
    var sirenLimitTime: Long,
    var roundingWeight: Double,
    var currentState: AtomicReference<CurrentState>,
    var scheduleCleaner: AtomicReference<ScheduleCleaner>,
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
                it.reactionTime = reactionTime
                it.sirenLimitTime = sirenLimitTime
                it.roundingWeight = roundingWeight
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

            handler {
                onEnv { status == CorStatus.STARTED }
                exec {
                    frame.channel = ModelFrame.Channels.CAMERA
                }
            }

            +EncodeBase64Handler
            +WsSendFrameHandler
            +WsSendMeltFinishHandler
            +FinishHandler
        }
    }
}
