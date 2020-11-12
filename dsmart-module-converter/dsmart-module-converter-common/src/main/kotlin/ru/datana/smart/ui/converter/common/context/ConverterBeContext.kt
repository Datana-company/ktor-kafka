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
    var lastTimeAngles: AtomicLong = AtomicLong(0),
    var lastTimeFrame: AtomicLong = AtomicLong(0),
    var lastTimeSlagRate: AtomicLong = AtomicLong(0),
    var status: CorStatus = CorStatus.STARTED,
    var errors: MutableList<CorError> = mutableListOf(),
    var timeStart: Instant = Instant.now(),
    var timeStop: Instant = Instant.now(),
    var wsManager: IWsManager = IWsManager.NONE,
    var metalRateCriticalPoint: Double = Double.MIN_VALUE,
    var metalRateWarningPoint: Double = Double.MIN_VALUE,
    var reactionTime: Long = Long.MIN_VALUE,
    var sirenLimitTime: Long = Long.MIN_VALUE,
    var dataTimeout: Long = Long.MIN_VALUE,
    var eventsRepository: IUserEventsRepository = IUserEventsRepository.NONE,
    var currentState: AtomicReference<CurrentState?> = AtomicReference(),
    var scheduleCleaner: AtomicReference<ScheduleCleaner?> = AtomicReference(),
    var converterId: String = "",
    var framesBasePath: String = ""
)
