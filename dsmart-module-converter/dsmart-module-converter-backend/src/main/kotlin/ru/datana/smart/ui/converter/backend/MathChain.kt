package ru.datana.smart.ui.converter.backend

import codes.spectrum.konveyor.DefaultKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.konveyor
import ru.datana.smart.logger.datanaLogger
import ru.datana.smart.ui.converter.backend.handlers.*
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.*
import ru.datana.smart.ui.converter.common.repositories.IUserEventsRepository
import java.util.concurrent.atomic.AtomicReference

class MathChain(
    var eventsRepository: IUserEventsRepository,
    var wsManager: IWsManager,
    var wsSignalerManager: IWsSignalerManager,
    var dataTimeout: Long,
    var metalRateCriticalPoint: Double,
    var metalRateWarningPoint: Double,
    var reactionTime: Long,
    var sirenLimitTime: Long,
    var currentState: AtomicReference<CurrentState>,
    var scheduleCleaner: AtomicReference<ScheduleCleaner>,
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
                it.wsSignalerManager= wsSignalerManager
                it.dataTimeout = dataTimeout
                it.metalRateCriticalPoint = metalRateCriticalPoint
                it.metalRateWarningPoint = metalRateWarningPoint
                it.reactionTime = reactionTime
                it.sirenLimitTime = sirenLimitTime
                it.currentState = currentState
                it.scheduleCleaner = scheduleCleaner
                it.converterId = converterId
                it.framesBasePath = framesBasePath
            },
            env
        )
    }

    companion object {

        val logger = datanaLogger(this::class.java)
        val konveyor = konveyor<ConverterBeContext> {

            +DevicesFilterHandler
            +MeltFilterHandler
            +FrameTimeFilterHandler

            handler {
                onEnv { status == CorStatus.STARTED }
                exec {
                    frame.channel = ModelFrame.Channels.MATH
                }
            }

            +EncodeBase64Handler
            +WsSendMathFrameHandler
            konveyor {
                // Временный фильтр на выбросы матмодели по содержанию металла из-за капель металла
                // в начале и в конце слива
                on {
                    val res = slagRate.steelRate <= 0.35
                    val sr = slagRate
                    val mi = meltInfo
                    if (! res) {
                        logger.debug("Filtering out slagRate due to too high value for steelRate", object {
                            val eventType: String = "dsmart-converter-ui-slagRate-filter-highsteel"
                            val slagRate: ModelSlagRate = sr
                            val meltInfo: ModelMeltInfo = mi
                        })
                    }
                    res
                }

                +WsSendMathSlagRateHandler

                // Обновляем информацию о последнем значении slagRate
                handler {
                    on { status == CorStatus.STARTED}
                    exec {
                        val curState = currentState.get() ?: CurrentState()
                        curState.lastSlagRate = slagRate
                        currentState.set(curState)
                    }
                }

                handler {
                    onEnv { status == CorStatus.STARTED }
                    exec {
                        EventsChain(
                            eventsRepository = eventsRepository,
                            wsManager = wsManager,
                            wsSignalerManager= wsSignalerManager,
                            dataTimeout = dataTimeout,
                            metalRateCriticalPoint = metalRateCriticalPoint,
                            metalRateWarningPoint = metalRateWarningPoint,
                            currentState = currentState,
                            scheduleCleaner = scheduleCleaner,
                            reactionTime = reactionTime,
                            sirenLimitTime = sirenLimitTime,
                            converterId = converterId
                        ).exec(this)
                    }
                }
            }
            +WsSendMeltFinishHandler

            +FinishHandler
        }
    }
}
