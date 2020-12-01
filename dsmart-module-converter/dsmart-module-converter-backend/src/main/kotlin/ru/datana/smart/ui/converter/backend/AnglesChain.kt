package ru.datana.smart.ui.converter.backend

import codes.spectrum.konveyor.DefaultKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.konveyor
import ru.datana.smart.ui.converter.backend.handlers.*
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.models.*
import ru.datana.smart.ui.converter.common.repositories.IUserEventsRepository
import java.util.concurrent.atomic.AtomicReference

class AnglesChain(
    var eventsRepository: IUserEventsRepository,
    var wsManager: IWsManager,
    var wsSignalerManager: IWsSignalerManager,
    var dataTimeout: Long,
    var meltTimeout: Long,
    var metalRateCriticalPoint: Double,
    var metalRateWarningPoint: Double,
    var reactionTime: Long,
    var sirenLimitTime: Long,
    var roundingWeight: Double,
    var currentState: AtomicReference<CurrentState>,
    var scheduleCleaner: AtomicReference<ScheduleCleaner>,
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
                it.wsSignalerManager = wsSignalerManager
                it.dataTimeout = dataTimeout
                it.meltTimeout = meltTimeout
                it.metalRateCriticalPoint = metalRateCriticalPoint
                it.metalRateWarningPoint = metalRateWarningPoint
                it.reactionTime = reactionTime
                it.sirenLimitTime = sirenLimitTime
                it.roundingWeight = roundingWeight
                it.currentState = currentState
                it.scheduleCleaner = scheduleCleaner
                it.converterId = converterId
            },
            env
        )
    }

    companion object {
        val konveyor = konveyor<ConverterBeContext> {

            +DevicesFilterHandler
            +MeltFilterHandler
            +AngleTimeFilterHandler

            +WsSendAnglesHandler

            +FinishHandler
        }
    }
}
