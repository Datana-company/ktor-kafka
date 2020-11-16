package ru.datana.smart.ui.converter.backend

import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.models.*
import ru.datana.smart.ui.converter.common.repositories.IUserEventsRepository
import java.util.concurrent.atomic.AtomicReference

class ConverterFacade(
    converterRepository: IUserEventsRepository = IUserEventsRepository.NONE,
    wsManager: IWsManager = IWsManager.NONE,
    wsSignalerManager: IWsSignalerManager = IWsSignalerManager.NONE,
    dataTimeout: Long = Long.MIN_VALUE,
    metalRateCriticalPoint: Double = Double.MIN_VALUE,
    metalRateWarningPoint: Double = Double.MIN_VALUE,
    reactionTime: Long = Long.MIN_VALUE,
    sirenLimitTime: Long = Long.MIN_VALUE,
    roundingWeight: Double = Double.MIN_VALUE,
    currentState: AtomicReference<CurrentState?> = AtomicReference(),
    scheduleCleaner: AtomicReference<ScheduleCleaner?> = AtomicReference(),
    converterId: String = "",
    framesBasePath: String = ""
) {
    private val mathChain = MathChain(
        eventsRepository = converterRepository,
        wsManager = wsManager,
        wsSignalerManager= wsSignalerManager,
        dataTimeout = dataTimeout,
        metalRateCriticalPoint = metalRateCriticalPoint,
        metalRateWarningPoint = metalRateWarningPoint,
        currentState = currentState,
        scheduleCleaner = scheduleCleaner,
        reactionTime = reactionTime,
        sirenLimitTime = sirenLimitTime,
        roundingWeight = roundingWeight,
        converterId = converterId,
        framesBasePath = framesBasePath
    )
    private val anglesChain = AnglesChain(
        eventsRepository = converterRepository,
        wsManager = wsManager,
        wsSignalerManager = wsSignalerManager,
        dataTimeout = dataTimeout,
        metalRateCriticalPoint = metalRateCriticalPoint,
        metalRateWarningPoint = metalRateWarningPoint,
        currentState = currentState,
        scheduleCleaner = scheduleCleaner,
        reactionTime = reactionTime,
        sirenLimitTime = sirenLimitTime,
        roundingWeight = roundingWeight,
        converterId = converterId
    )
    private val frameChain = FrameChain(
        eventsRepository = converterRepository,
        wsManager = wsManager,
        dataTimeout = dataTimeout,
        metalRateCriticalPoint = metalRateCriticalPoint,
        metalRateWarningPoint = metalRateWarningPoint,
        currentState = currentState,
        scheduleCleaner = scheduleCleaner,
        reactionTime = reactionTime,
        sirenLimitTime = sirenLimitTime,
        roundingWeight = roundingWeight,
        converterId = converterId,
        framesBasePath = framesBasePath
    )
    private val meltInfoChain = MeltInfoChain(
        eventsRepository = converterRepository,
        wsManager = wsManager,
        dataTimeout = dataTimeout,
        metalRateCriticalPoint = metalRateCriticalPoint,
        metalRateWarningPoint = metalRateWarningPoint,
        currentState = currentState,
        scheduleCleaner = scheduleCleaner,
        reactionTime = reactionTime,
        sirenLimitTime = sirenLimitTime,
        roundingWeight = roundingWeight,
        converterId = converterId
    )

    suspend fun handleMath(context: ConverterBeContext) = mathChain.exec(context)
    suspend fun handleAngles(context: ConverterBeContext) = anglesChain.exec(context)
    suspend fun handleFrame(context: ConverterBeContext) = frameChain.exec(context)
    suspend fun handleMeltInfo(context: ConverterBeContext) = meltInfoChain.exec(context)
}
