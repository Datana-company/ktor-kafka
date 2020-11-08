package ru.datana.smart.ui.converter.backend

import codes.spectrum.konveyor.DefaultKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.konveyor
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.IWsManager
import ru.datana.smart.ui.converter.common.models.ModelMeltInfo
import ru.datana.smart.ui.converter.common.repositories.IUserEventsRepository
import java.util.concurrent.atomic.AtomicReference

class ExtEventsChain(
    var eventsRepository: IUserEventsRepository,
    var wsManager: IWsManager,
    var metalRateCriticalPoint: Double,
    var metalRateWarningPoint: Double,
    var currentMeltInfo: AtomicReference<ModelMeltInfo?>,
    var timeReaction: Long,
    var timeLimitSiren: Long,
    var converterId: String
) {

    suspend fun exec(context: ConverterBeContext) {
        exec(context, DefaultKonveyorEnvironment)
    }

    suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        konveyor.exec(
            context.also {
                it.eventsRepository = eventsRepository
                it.wsManager = wsManager
                it.metalRateCriticalPoint = metalRateCriticalPoint
                it.metalRateWarningPoint = metalRateWarningPoint
                it.currentMeltInfo = currentMeltInfo
                it.timeReaction = timeReaction
                it.timeLimitSiren = timeLimitSiren
                it.converterId = converterId
            },
            env
        )
    }

    companion object {
        val konveyor = konveyor<ConverterBeContext> {

            timeout { 1000 }

            // Тут какие-то хэндлеры для обработки внешнего эвента (Какие, пока не понятно?)

            handler {
                onEnv { status == CorStatus.STARTED }
                exec {
                    // Передаём данные в мэнеджер вэб сокетов для последующей отправки
                    wsManager.sendExtEvents(this)
                }
            }

            exec {
                EventsChain(
                    eventsRepository = eventsRepository,
                    wsManager = wsManager,
                    metalRateCriticalPoint = metalRateCriticalPoint,
                    metalRateWarningPoint = metalRateWarningPoint,
                    currentMeltInfo = currentMeltInfo,
                    timeReaction = timeReaction,
                    timeLimitSiren = timeLimitSiren,
                    converterId = converterId
                ).exec(this)
            }
        }
    }
}
