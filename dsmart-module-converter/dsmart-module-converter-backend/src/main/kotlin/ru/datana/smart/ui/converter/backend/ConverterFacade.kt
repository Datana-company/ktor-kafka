package ru.datana.smart.ui.converter.backend

import ru.datana.smart.ui.converter.backend.common.ConverterChainSettings
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.models.CurrentState
import ru.datana.smart.ui.converter.common.models.IWsManager
import ru.datana.smart.ui.converter.common.models.IWsSignalerManager
import ru.datana.smart.ui.converter.common.models.IConverterFacade
import ru.datana.smart.ui.converter.common.models.ScheduleCleaner
import ru.datana.smart.ui.converter.common.repositories.IEventRepository
import ru.datana.smart.ui.converter.common.models.ModelEventMode
import ru.datana.smart.ui.converter.common.repositories.ICurrentStateRepository
import java.util.concurrent.atomic.AtomicReference

class ConverterFacade(
    currentStateRepository: ICurrentStateRepository = ICurrentStateRepository.NONE,
    converterRepository: IEventRepository = IEventRepository.NONE,
    wsManager: IWsManager = IWsManager.NONE,
    wsSignalerManager: IWsSignalerManager = IWsSignalerManager.NONE,
    dataTimeout: Long = Long.MIN_VALUE,
    meltTimeout: Long = Long.MIN_VALUE,
    eventMode: ModelEventMode = ModelEventMode.STEEL,
    streamRateCriticalPoint: Double = Double.MIN_VALUE,
    streamRateWarningPoint: Double = Double.MIN_VALUE,
    reactionTime: Long = Long.MIN_VALUE,
    sirenLimitTime: Long = Long.MIN_VALUE,
    roundingWeight: Double = Double.MIN_VALUE,
    currentState: AtomicReference<CurrentState> = AtomicReference(),
    scheduleCleaner: AtomicReference<ScheduleCleaner> = AtomicReference(),
    converterId: String = "",
    framesBasePath: String = ""

): IConverterFacade {
    private val chainSettings = ConverterChainSettings(
        currentStateRepository = currentStateRepository,
        eventsRepository = converterRepository,
        wsManager = wsManager,
        wsSignalerManager= wsSignalerManager,
        dataTimeout = dataTimeout,
        meltTimeout = meltTimeout,
        eventMode = eventMode,
        streamRateCriticalPoint = streamRateCriticalPoint,
        streamRateWarningPoint = streamRateWarningPoint,
        currentState = currentState,
        scheduleCleaner = scheduleCleaner,
        reactionTime = reactionTime,
        sirenLimitTime = sirenLimitTime,
        roundingWeight = roundingWeight,
        converterId = converterId,
        framesBasePath = framesBasePath,
        converterFacade = this
    )

    private val mathChain = MathChain(
        chainSettings = chainSettings
    )
    private val anglesChain = AnglesChain(
        chainSettings = chainSettings
    )
    private val frameChain = FrameChain(
        chainSettings = chainSettings
    )
    private val meltInfoChain = MeltInfoChain(
        chainSettings = chainSettings
    )
    private val steelEventsChain = SteelEventsChain(
        chainSettings = chainSettings
    )
    private val slagEventsChain = SlagEventsChain(
        chainSettings = chainSettings
    )
    private val externalEventsChain = ExternalEventsChain(
        chainSettings = chainSettings
    )
    private val signalerChain = SignalerChain(
        chainSettings = chainSettings
    )

    override suspend fun handleMath(context: ConverterBeContext) = mathChain.exec(context)
    override suspend fun handleAngles(context: ConverterBeContext) = anglesChain.exec(context)
    override suspend fun handleFrame(context: ConverterBeContext) = frameChain.exec(context)
    override suspend fun handleMeltInfo(context: ConverterBeContext) = meltInfoChain.exec(context)
    override suspend fun handleSteelEvents(context: ConverterBeContext) = steelEventsChain.exec(context)
    override suspend fun handleSlagEvents(context: ConverterBeContext) = slagEventsChain.exec(context)
    override suspend fun handleExternalEvents(context: ConverterBeContext) = externalEventsChain.exec(context)
    override suspend fun handleSignaler(context: ConverterBeContext) = signalerChain.exec(context)
}
