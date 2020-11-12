package ru.datana.smart.ui.converter.backend

import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.models.CurrentState
import ru.datana.smart.ui.converter.common.models.IWsManager
import ru.datana.smart.ui.converter.common.models.ScheduleCleaner
import ru.datana.smart.ui.converter.common.repositories.IEventRepository
import java.util.concurrent.atomic.AtomicReference

class ConverterFacade(
    converterRepository: IEventRepository = IEventRepository.NONE,
    wsManager: IWsManager = IWsManager.NONE,
    dataTimeout: Long = Long.MIN_VALUE,
    metalRateCriticalPoint: Double = Double.MIN_VALUE,
    metalRateWarningPoint: Double = Double.MIN_VALUE,
    timeReaction: Long = Long.MIN_VALUE,
    timeLimitSiren: Long = Long.MIN_VALUE,
    currentState: AtomicReference<CurrentState?> = AtomicReference(),
    scheduleCleaner: AtomicReference<ScheduleCleaner?> = AtomicReference(),
    converterId: String = "",
    framesBasePath: String = ""
) {
    private val mathChain = MathChain(
        eventsRepository = converterRepository,
        wsManager = wsManager,
        dataTimeout = dataTimeout,
        metalRateCriticalPoint = metalRateCriticalPoint,
        metalRateWarningPoint = metalRateWarningPoint,
        currentState = currentState,
        scheduleCleaner = scheduleCleaner,
        timeReaction = timeReaction,
        timeLimitSiren = timeLimitSiren,
        converterId = converterId,
        framesBasePath = framesBasePath
    )
    private val anglesChain = AnglesChain(
        eventsRepository = converterRepository,
        wsManager = wsManager,
        dataTimeout = dataTimeout,
        metalRateCriticalPoint = metalRateCriticalPoint,
        metalRateWarningPoint = metalRateWarningPoint,
        currentState = currentState,
        scheduleCleaner = scheduleCleaner,
        timeReaction = timeReaction,
        timeLimitSiren = timeLimitSiren,
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
        timeReaction = timeReaction,
        timeLimitSiren = timeLimitSiren,
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
        timeReaction = timeReaction,
        timeLimitSiren = timeLimitSiren,
        converterId = converterId
    )
    private val extEventsChain = ExtEventsChain(
        eventsRepository = converterRepository,
        wsManager = wsManager,
        dataTimeout = dataTimeout,
        metalRateCriticalPoint = metalRateCriticalPoint,
        metalRateWarningPoint = metalRateWarningPoint,
        currentState = currentState,
        scheduleCleaner = scheduleCleaner,
        timeReaction = timeReaction,
        timeLimitSiren = timeLimitSiren,
        converterId = converterId
    )

    suspend fun handleMath(context: ConverterBeContext) = mathChain.exec(context)
    suspend fun handleAngles(context: ConverterBeContext) = anglesChain.exec(context)
    suspend fun handleFrame(context: ConverterBeContext) = frameChain.exec(context)
    suspend fun handleMeltInfo(context: ConverterBeContext) = meltInfoChain.exec(context)
    suspend fun handleExtEvents(context: ConverterBeContext) = extEventsChain.exec(context)
}
