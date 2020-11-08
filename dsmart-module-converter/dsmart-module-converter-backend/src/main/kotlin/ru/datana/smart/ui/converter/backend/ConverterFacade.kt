package ru.datana.smart.ui.converter.backend

import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.models.IWsManager
import ru.datana.smart.ui.converter.common.models.ModelMeltInfo
import ru.datana.smart.ui.converter.common.repositories.IUserEventsRepository
import java.util.concurrent.atomic.AtomicReference

class ConverterFacade(
    converterRepository: IUserEventsRepository = IUserEventsRepository.NONE,
    wsManager: IWsManager = IWsManager.NONE,
    metalRateCriticalPoint: Double = Double.MIN_VALUE,
    metalRateWarningPoint: Double = Double.MIN_VALUE,
    timeReaction: Long = Long.MIN_VALUE,
    timeLimitSiren: Long = Long.MIN_VALUE,
    currentMeltInfo: AtomicReference<ModelMeltInfo?> = AtomicReference(),
    converterId: String = "",
    framesBasePath: String = ""
) {
    private val mathChain = MathChain(
        eventsRepository = converterRepository,
        wsManager = wsManager,
        metalRateCriticalPoint = metalRateCriticalPoint,
        metalRateWarningPoint = metalRateWarningPoint,
        currentMeltInfo = currentMeltInfo,
        timeReaction = timeReaction,
        timeLimitSiren = timeLimitSiren,
        converterId = converterId,
        framesBasePath = framesBasePath
    )
    private val anglesChain = AnglesChain(
        eventsRepository = converterRepository,
        wsManager = wsManager,
        metalRateCriticalPoint = metalRateCriticalPoint,
        metalRateWarningPoint = metalRateWarningPoint,
        currentMeltInfo = currentMeltInfo,
        timeReaction = timeReaction,
        timeLimitSiren = timeLimitSiren,
        converterId = converterId
    )
    private val frameChain = FrameChain(
        eventsRepository = converterRepository,
        wsManager = wsManager,
        metalRateCriticalPoint = metalRateCriticalPoint,
        metalRateWarningPoint = metalRateWarningPoint,
        currentMeltInfo = currentMeltInfo,
        timeReaction = timeReaction,
        timeLimitSiren = timeLimitSiren,
        converterId = converterId,
        framesBasePath = framesBasePath
    )
    private val meltInfoChain = MeltInfoChain(
        eventsRepository = converterRepository,
        wsManager = wsManager,
        metalRateCriticalPoint = metalRateCriticalPoint,
        metalRateWarningPoint = metalRateWarningPoint,
        currentMeltInfo = currentMeltInfo,
        timeReaction = timeReaction,
        timeLimitSiren = timeLimitSiren,
        converterId = converterId
    )
    private val extEventsChain = ExtEventsChain(
        eventsRepository = converterRepository,
        wsManager = wsManager,
        metalRateCriticalPoint = metalRateCriticalPoint,
        metalRateWarningPoint = metalRateWarningPoint,
        currentMeltInfo = currentMeltInfo,
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
