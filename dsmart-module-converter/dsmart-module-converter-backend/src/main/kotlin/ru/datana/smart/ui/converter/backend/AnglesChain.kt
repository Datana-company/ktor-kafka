package ru.datana.smart.ui.converter.backend

import codes.spectrum.konveyor.DefaultKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.konveyor
import ru.datana.smart.ui.converter.backend.handlers.*
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.IWsManager
import ru.datana.smart.ui.converter.common.models.ModelMeltInfo
import ru.datana.smart.ui.converter.common.repositories.IUserEventsRepository
import java.util.concurrent.atomic.AtomicReference

class AnglesChain(
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

            +DevicesFilterHandler
            +MeltFilterHandler

            handler {
                onEnv { status == CorStatus.STARTED }
                exec {
                    wsManager.sendAngles(this)
                }
            }

            exec {
                EventsChain(
                    eventsRepository = eventsRepository,
                    wsManager = wsManager,
                    metalRateCriticalPoint = metalRateCriticalPoint,
                    metalRateWarningPoint = metalRateWarningPoint,
                    currentMeltInfo = currentMeltInfo,
                    converterId = converterId
                ).exec(this)
            }
        }
    }
}
