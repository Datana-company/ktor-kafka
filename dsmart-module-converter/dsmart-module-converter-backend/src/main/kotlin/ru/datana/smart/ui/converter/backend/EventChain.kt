package ru.datana.smart.ui.converter.backend

import codes.spectrum.konveyor.DefaultKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.konveyor
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.backend.handlers.MetalRateCriticalHandler
import ru.datana.smart.ui.converter.backend.handlers.MetalRateExceedsHandler
import ru.datana.smart.ui.converter.backend.handlers.MetalRateInfoHandler
import ru.datana.smart.ui.converter.backend.handlers.MetalRateNormalHandler
import ru.datana.smart.ui.converter.common.models.IWsManager
import ru.datana.smart.ui.converter.common.models.ModelMeltInfo
import ru.datana.smart.ui.converter.common.repositories.IUserEventsRepository
import java.util.concurrent.atomic.AtomicReference

class EventChain(
    var eventsRepository: IUserEventsRepository,
    var wsManager: IWsManager,
    var metalRateCriticalPoint: Double,
    var metalRateWarningPoint: Double,
    var converterDeviceId: String,
    var currentMeltInfo: AtomicReference<ModelMeltInfo?>
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
                it.converterDeviceId = converterDeviceId
                it.currentMeltInfo = currentMeltInfo
            },
            env
        )
    }

    companion object {
        val konveyor = konveyor<ConverterBeContext> {
            +MetalRateCriticalHandler
            +MetalRateExceedsHandler
            +MetalRateNormalHandler
            +MetalRateInfoHandler
        }
    }
}
