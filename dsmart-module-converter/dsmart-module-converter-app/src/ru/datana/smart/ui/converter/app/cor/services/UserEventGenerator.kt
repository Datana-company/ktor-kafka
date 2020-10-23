package ru.datana.smart.ui.converter.app.cor.services

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.konveyor
import ru.datana.smart.ui.converter.app.cor.context.ConverterBeContext
import ru.datana.smart.ui.converter.app.cor.handlers.MetalRateCriticalHandler
import ru.datana.smart.ui.converter.app.cor.handlers.MetalRateExceedsHandler
import ru.datana.smart.ui.converter.app.cor.handlers.MetalRateInfoHandler
import ru.datana.smart.ui.converter.app.cor.handlers.MetalRateNormalHandler

class UserEventGenerator {

    suspend fun exec(context: ConverterBeContext<String, String>, env: IKonveyorEnvironment) {
        konveyor.exec(context, env)
    }

    companion object {
        val konveyor = konveyor<ConverterBeContext<String, String>> {
            +MetalRateCriticalHandler
            +MetalRateExceedsHandler
            +MetalRateNormalHandler
            +MetalRateInfoHandler
        }
    }
}
