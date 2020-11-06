package ru.datana.smart.ui.converter.backend

import codes.spectrum.konveyor.DefaultKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.konveyor
import ru.datana.smart.ui.converter.backend.handlers.*
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.IWsManager
import ru.datana.smart.ui.converter.common.models.ModelEvents
import ru.datana.smart.ui.converter.common.models.ModelMeltInfo
import ru.datana.smart.ui.converter.common.repositories.IUserEventsRepository
import java.util.concurrent.atomic.AtomicReference

class EventsChain(
    var eventsRepository: IUserEventsRepository,
    var wsManager: IWsManager,
    var metalRateCriticalPoint: Double,
    var metalRateWarningPoint: Double,
    var currentMeltInfo: AtomicReference<ModelMeltInfo?>,
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
                it.converterId = converterId
            },
            env
        )
    }

    companion object {
        val konveyor = konveyor<ConverterBeContext> {

            timeout { 1000 }

            konveyor {
                on { slagRate.steelRate?.let { it >= metalRateCriticalPoint  } ?: false }
                +UpdateWarningEventHandler
                +UpdateInfoEventHandler
                +UpdateEndEventHandler
                +CreateCriticalEventHandler
            }
            konveyor {
                on { slagRate.steelRate?.let { it > metalRateWarningPoint && it < metalRateCriticalPoint } ?: false }
                +UpdateCriticalEventHandler
                +UpdateInfoEventHandler
                +UpdateEndEventHandler
                +CreateWarningEventHandler
            }
            konveyor {
                on { slagRate.steelRate?.let { it <= metalRateWarningPoint && it > 0.0 } ?: false }
                +UpdateCriticalEventHandler
                +UpdateWarningEventHandler
                +UpdateEndEventHandler
                +CreateInfoEventHandler
            }
            konveyor {
                on { slagRate.steelRate?.let { it == 0.0 } ?: false && slagRate.slagRate?.let { it == 0.0 } ?: false }
                +UpdateCriticalEventHandler
                +UpdateWarningEventHandler
                +UpdateInfoEventHandler
                +CreateSuccessMeltEventHandler
                +CreateEndEventHandler
            }
            konveyor {
                on { angles.angle != null }
                +UpdateAngleCriticalEventHandler
                +UpdateAngleWarningEventHandler
                +UpdateAngleInfoEventHandler
            }
            handler {
                onEnv { status == CorStatus.STARTED && currentMeltInfo.get()?.let { it.id != null } ?: false }
                exec {
                    val meltId: String = currentMeltInfo.get()!!.id!!
                    events = ModelEvents(events = eventsRepository.getAllByMeltId(meltId))
                }
            }

            handler {
                onEnv { status == CorStatus.STARTED }
                exec {
                    wsManager.sendEvents(this)
                }
            }
        }
    }
}
