package ru.datana.smart.ui.converter.backend

import codes.spectrum.konveyor.DefaultKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.konveyor
import ru.datana.smart.ui.converter.backend.common.ConverterChainSettings
import ru.datana.smart.ui.converter.backend.common.setSettings
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.backend.handlers.*
import ru.datana.smart.ui.converter.common.context.CorStatus

/*
* MeltInfoChain - цепочка обработки данных о начале плавки.
* */
class MeltInfoChain(
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

            +DevicesFilterHandler // фильтр данных по идетификатору устройства
            +SetCurrentMeltInfoHandler // задаём текущие данные о плавке

            // отправка данных о плавке на фронтенд по web-socket
            handler {
                onEnv { status == CorStatus.STARTED }
                exec {
                    wsManager.sendMeltInfo(this)
                }
            }

            +FinishHandler // обработчик завершения цепочки
        }
    }
}
