package ru.datana.smart.ui.converter.common.context

import ru.datana.smart.ui.converter.common.models.*
import ru.datana.smart.ui.converter.common.repositories.IUserEventsRepository
import java.time.Instant
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference

class ConverterBeContext (

    var angles: ModelAngles = ModelAngles.NONE,
    var meltInfo: ModelMeltInfo = ModelMeltInfo.NONE,
    var frame: ModelFrame = ModelFrame.NONE,
    var slagRate: ModelSlagRate = ModelSlagRate.NONE,
    var events: ModelEvents = ModelEvents.NONE,
    // внутренняя модель (dsmart-module-converter-common.models)
    var extEvents: ModelExtEvents = ModelExtEvents.NONE,
    var lastTimeProc: AtomicLong = AtomicLong(0),
    var status: CorStatus = CorStatus.STARTED,
    var errors: MutableList<CorError> = mutableListOf(),
    var timeStart: Instant = Instant.now(),
    var timeStop: Instant = Instant.now(),
    var wsManager: IWsManager = IWsManager.NONE,
    var metalRateCriticalPoint: Double = Double.MIN_VALUE,
    var metalRateWarningPoint: Double = Double.MIN_VALUE,
    var eventsRepository: IUserEventsRepository = IUserEventsRepository.NONE,
    var currentMeltInfo: AtomicReference<ModelMeltInfo?> = AtomicReference(),
    var converterId: String = ""
)
