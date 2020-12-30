package ru.datana.smart.ui.converter.backend

import codes.spectrum.konveyor.DefaultKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.konveyor
import ru.datana.smart.ui.converter.backend.common.ConverterChainSettings
import ru.datana.smart.ui.converter.backend.common.setSettings
import ru.datana.smart.ui.converter.backend.handlers.*
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.*

/*
* SteelEventsChain - цепочка обработки по металлу.
* */
class SteelEventsChain(
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

            +GetActiveEventHandler // из репозитория извлекается активное событие
            +SetStreamStatus // устанавливается статус содержания потока

            // конвейер обработки события "Критическая ситуация"
            konveyor {
                on { streamStatus == ModelStreamStatus.CRITICAL }
                +SetEventExecutionStatusHandler // задаётся статус выполнения у текущего события
                +SetEventInactiveStatusHandler // задаётся статус активности у текущего события
                +UpdateEventHandler // обновление текущего события
                +CreateCriticalSteelEventHandler // создание события "Критическая ситуация"
            }
            // конвейер обработки события "Предупреждения"
            konveyor {
                on { streamStatus == ModelStreamStatus.WARNING }
                +SetEventExecutionStatusHandler // задаётся статус выполнения у текущего события
                +SetEventInactiveStatusHandler // задаётся статус активности у текущего события
                +UpdateEventHandler // обновление текущего события
                +CreateWarningSteelEventHandler // создание события "Предупреждение"
            }
//            // конвейер обработки события "Информация"
//            konveyor {
//                on { streamStatus == ModelStreamStatus.INFO }
//                +SetEventExecutionStatusHandler // задаётся статус выполнения у текущего события
//                +SetEventInactiveStatusHandler // задаётся статус активности у текущего события
//                +UpdateEventHandler // обновление текущего события
//                +CreateInfoSteelEventHandler // создание события "Информация"
//            }
            // конвейер событий, когда металл в потоке не превышает или равен норме
            konveyor {
                on { streamStatus == ModelStreamStatus.NORMAL }
                +SetEventExecutionStatusHandler // задаётся статус выполнения у текущего события
                +SetEventInactiveStatusHandler // задаётся статус активности у текущего события
                +UpdateEventHandler // обновление текущего события
            }
            // конвейер событий при завершении плавки
            konveyor {
                on { streamStatus == ModelStreamStatus.END }
                +SetEventInactiveStatusHandler // задаётся статус активности у текущего события
                +UpdateEventHandler // обновление текущего события
                +CreateSuccessMeltSteelEventHandler // создание события об успешном завершении плавки
            }

            // достаются все события, касающиеся текущей плавки
            handler {
                onEnv { status == CorStatus.STARTED }
                exec {
                    eventList = eventRepository.getAllByMeltId(meltInfo.id)
                }
            }

            // отправка событий на фронтенд по web-socket
            handler {
                onEnv { status == CorStatus.STARTED }
                exec {
                    wsManager.sendEvents(this)
                }
            }
        }
    }
}
