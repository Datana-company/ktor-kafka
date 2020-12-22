package ru.datana.smart.ui.converter.backend

import codes.spectrum.konveyor.DefaultKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.konveyor
import ru.datana.smart.ui.converter.backend.common.ConverterChainSettings
import ru.datana.smart.ui.converter.backend.common.setSettings
import ru.datana.smart.ui.converter.backend.handlers.*
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.ModelStreamStatus

class SignalerChain(
    var chainSettings: ConverterChainSettings
) {

    suspend fun exec(context: ConverterBeContext) {
        exec(context, DefaultKonveyorEnvironment)
    }

    suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        context.setSettings(chainSettings)
        konveyor.exec(context, env)
    }

    companion object {
        val konveyor = konveyor<ConverterBeContext> {

            +SetStreamStatus

            konveyor {
                on { streamStatus == ModelStreamStatus.CRITICAL }
                +CriticalSignalizationHandler
            }
            konveyor {
                on { streamStatus == ModelStreamStatus.WARNING }
                +WarningSignalizationHandler
            }
//            konveyor {
//                on { streamStatus == ModelStreamStatus.INFO }
//                +InfoSignalizationHandler
//            }
            konveyor {
                on { streamStatus == ModelStreamStatus.NORMAL || streamStatus == ModelStreamStatus.NONE }
                +NormalSignalizationHandler
            }
            handler {
                onEnv { status == CorStatus.STARTED }
                exec {
                    wsSignalerManager.sendSignaler(this)
                }
            }
        }
    }
}
