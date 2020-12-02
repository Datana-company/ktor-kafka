package ru.datana.smart.ui.converter.common.context

import ru.datana.smart.ui.converter.common.models.ModelEventMode
import ru.datana.smart.ui.converter.common.models.*
import ru.datana.smart.ui.converter.common.repositories.IEventRepository
import java.time.Instant
import java.util.concurrent.atomic.AtomicReference

data class ConverterBeContext(

    var angles: ModelAngles = ModelAngles.NONE,
    var meltInfo: ModelMeltInfo = ModelMeltInfo.NONE,
    var frame: ModelFrame = ModelFrame.NONE,
    var slagRate: ModelSlagRate = ModelSlagRate.NONE,
    var events: MutableList<ModelEvent> = mutableListOf(),
    // внутренняя модель (dsmart-module-converter-common.models)
    var extEvents: ModelExtEvents = ModelExtEvents.NONE,
    var lastTimeAngles: AtomicReference<Instant> = AtomicReference(Instant.EPOCH),
    var lastTimeFrame: AtomicReference<Instant> = AtomicReference(Instant.EPOCH),
    var status: CorStatus = CorStatus.STARTED,
    var errors: MutableList<CorError> = mutableListOf(),
    var timeStart: Instant = Instant.now(),
    var timeStop: Instant = Instant.now(),
    var wsManager: IWsManager = IWsManager.NONE,
    var wsSignalerManager: IWsSignalerManager = IWsSignalerManager.NONE,
    var eventMode: ModelEventMode = ModelEventMode.STEEL,
    var streamRateCriticalPoint: Double = Double.MIN_VALUE,
    var streamRateWarningPoint: Double = Double.MIN_VALUE,
    var reactionTime: Long = Long.MIN_VALUE,
    var sirenLimitTime: Long = Long.MIN_VALUE,
    var roundingWeight: Double = Double.MIN_VALUE,
    var dataTimeout: Long = Long.MIN_VALUE,
    var meltTimeout: Long = Long.MIN_VALUE,
    var eventsRepository: IEventRepository = IEventRepository.NONE,
    var currentState: AtomicReference<CurrentState> = AtomicReference(CurrentState.NONE),
    var scheduleCleaner: AtomicReference<ScheduleCleaner> = AtomicReference(ScheduleCleaner.NONE),
    var signaler: SignalerModel = SignalerModel.NONE,
    var converterId: String = "",
    var framesBasePath: String = "",
    var converterFacade: IConverterFacade = IConverterFacade.NONE
)
