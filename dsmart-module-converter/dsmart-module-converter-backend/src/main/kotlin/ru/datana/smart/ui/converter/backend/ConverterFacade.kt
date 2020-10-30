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
    converterDeviceId: String = "",
    currentMeltInfo: AtomicReference<ModelMeltInfo?> = AtomicReference()
) {
    private val analysisChain = SlagRateChain(
        eventsRepository = converterRepository,
        wsManager = wsManager,
        metalRateCriticalPoint = metalRateCriticalPoint,
        metalRateWarningPoint = metalRateWarningPoint,
        converterDeviceId = converterDeviceId,
        currentMeltInfo = currentMeltInfo
    )
    private val angleChain = AnglesChain(
        eventsRepository = converterRepository,
        wsManager = wsManager,
        metalRateCriticalPoint = metalRateCriticalPoint,
        metalRateWarningPoint = metalRateWarningPoint,
        converterDeviceId = converterDeviceId,
        currentMeltInfo = currentMeltInfo
    )
    private val frameChain = FrameChain(
        eventsRepository = converterRepository,
        wsManager = wsManager,
        metalRateCriticalPoint = metalRateCriticalPoint,
        metalRateWarningPoint = metalRateWarningPoint,
        converterDeviceId = converterDeviceId,
        currentMeltInfo = currentMeltInfo
    )
    private val meltInfoChain = MetlInfoChain(
        eventsRepository = converterRepository,
        wsManager = wsManager,
        metalRateCriticalPoint = metalRateCriticalPoint,
        metalRateWarningPoint = metalRateWarningPoint,
        converterDeviceId = converterDeviceId,
        currentMeltInfo = currentMeltInfo
    )
    private val temperatureChain = TemperatureChain(
        eventsRepository = converterRepository,
        wsManager = wsManager,
        metalRateCriticalPoint = metalRateCriticalPoint,
        metalRateWarningPoint = metalRateWarningPoint,
        converterDeviceId = converterDeviceId,
        currentMeltInfo = currentMeltInfo
    )

    suspend fun handleSlagRate(context: ConverterBeContext) = analysisChain.exec(context)
    suspend fun handleAngle(context: ConverterBeContext) = angleChain.exec(context)
    suspend fun handleFrame(context: ConverterBeContext) = frameChain.exec(context)
    suspend fun handleMeltInfo(context: ConverterBeContext) = meltInfoChain.exec(context)
    suspend fun handleTemperature(context: ConverterBeContext) = temperatureChain.exec(context)
}
