package ru.datana.smart.ui.converter.backend

import codes.spectrum.konveyor.DefaultKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.konveyor
import ru.datana.smart.ui.converter.backend.handlers.*
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.IWsManager
import ru.datana.smart.ui.converter.common.models.ModelFrame
import ru.datana.smart.ui.converter.common.models.ModelMeltInfo
import ru.datana.smart.ui.converter.common.repositories.IUserEventsRepository
import java.util.concurrent.atomic.AtomicReference

class MathChain(
    var eventsRepository: IUserEventsRepository,
    var wsManager: IWsManager,
    var metalRateCriticalPoint: Double,
    var metalRateWarningPoint: Double,
    var timeReaction: Long,
    var timeLimitSiren: Long,
    var currentMeltInfo: AtomicReference<ModelMeltInfo?>,
    var converterId: String,
    var framesBasePath: String
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
                it.timeReaction = timeReaction
                it.timeLimitSiren = timeLimitSiren
                it.currentMeltInfo = currentMeltInfo
                it.converterId = converterId
                it.framesBasePath = framesBasePath
            },
            env
        )
    }

    companion object {
        val konveyor = konveyor<ConverterBeContext> {

            +DevicesFilterHandler
            +MeltFilterHandler
//            +SlagRateTimeFilterHandler

            handler {
                onEnv { status == CorStatus.STARTED }
                exec {
                    frame.channel = ModelFrame.Channels.MATH
                }
            }

            +EncodeBase64Handler

            handler {
                onEnv { status == CorStatus.STARTED }
                exec {
                    wsManager.sendSlagRate(this)
                    wsManager.sendFrames(this)
                }
            }

            handler {
                onEnv { status == CorStatus.STARTED }
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

            +FinishHandler
        }
    }
}
