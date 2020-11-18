package ru.datana.smart.ui.converter.backend.common

import ru.datana.smart.ui.converter.backend.ConverterFacade
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.models.CurrentState
import ru.datana.smart.ui.converter.common.models.IConverterFacade
import ru.datana.smart.ui.converter.common.models.IWsManager
import ru.datana.smart.ui.converter.common.models.ScheduleCleaner
import ru.datana.smart.ui.converter.common.repositories.IUserEventsRepository
import java.util.concurrent.atomic.AtomicReference

class ConverterChainSettings(
    var eventsRepository: IUserEventsRepository = IUserEventsRepository.NONE,
    var wsManager: IWsManager = IWsManager.NONE,
    var dataTimeout: Long = Long.MIN_VALUE,
    var metalRateCriticalPoint: Double = Double.MIN_VALUE,
    var metalRateWarningPoint: Double = Double.MIN_VALUE,
    var timeReaction: Long = Long.MIN_VALUE,
    var timeLimitSiren: Long = Long.MIN_VALUE,
    var currentState: AtomicReference<CurrentState?> = AtomicReference(),
    var scheduleCleaner: AtomicReference<ScheduleCleaner?> = AtomicReference(),
    var converterId: String = "",
    var framesBasePath: String = "",
    var converterFacade: IConverterFacade = IConverterFacade.NONE
)

fun ConverterBeContext.setSettings(converterChainSettings: ConverterChainSettings) {
    this.eventsRepository = converterChainSettings.eventsRepository
    this.wsManager = converterChainSettings.wsManager
    this.dataTimeout = converterChainSettings.dataTimeout
    this.metalRateCriticalPoint = converterChainSettings.metalRateCriticalPoint
    this.metalRateWarningPoint = converterChainSettings.metalRateWarningPoint
    this.timeReaction = converterChainSettings.timeReaction
    this.timeLimitSiren = converterChainSettings.timeLimitSiren
    this.currentState = converterChainSettings.currentState
    this.scheduleCleaner = converterChainSettings.scheduleCleaner
    this.converterId = converterChainSettings.converterId
    this.framesBasePath = converterChainSettings.framesBasePath
    this.converterFacade = converterChainSettings.converterFacade
}
