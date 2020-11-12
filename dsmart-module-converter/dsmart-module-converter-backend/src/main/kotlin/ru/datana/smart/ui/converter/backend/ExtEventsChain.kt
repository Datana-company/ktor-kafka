package ru.datana.smart.ui.converter.backend

import codes.spectrum.konveyor.DefaultKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.konveyor
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.models.CurrentState
import ru.datana.smart.ui.converter.common.models.IWsManager
import ru.datana.smart.ui.converter.common.models.ScheduleCleaner
import ru.datana.smart.ui.converter.common.repositories.IEventRepository
import java.util.concurrent.atomic.AtomicReference

class ExtEventsChain(
    var eventsRepository: IEventRepository,
    var wsManager: IWsManager,
    var dataTimeout: Long,
    var metalRateCriticalPoint: Double,
    var metalRateWarningPoint: Double,
    var timeReaction: Long,
    var timeLimitSiren: Long,
    var currentState: AtomicReference<CurrentState?>,
    var scheduleCleaner: AtomicReference<ScheduleCleaner?>,
    var converterId: String,
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
                it.timeReaction = timeReaction
                it.timeLimitSiren = timeLimitSiren
                it.currentState = currentState
                it.scheduleCleaner = scheduleCleaner
                it.converterId = converterId
            },
            env
        )
    }

    companion object {
        val konveyor = konveyor<ConverterBeContext> {

            timeout { 1000 }

            // Тут какие-то хэндлеры для обработки внешнего эвента (Какие, пока не понятно)

            exec {
                EventsChain(
                    eventsRepository = eventsRepository,
                    wsManager = wsManager,
                    dataTimeout = dataTimeout,
                    metalRateCriticalPoint = metalRateCriticalPoint,
                    metalRateWarningPoint = metalRateWarningPoint,
                    currentState = currentState,
                    scheduleCleaner = scheduleCleaner,
                    timeReaction = timeReaction,
                    timeLimitSiren = timeLimitSiren,
                    converterId = converterId
                ).exec(this)
            }
        }
    }
}
