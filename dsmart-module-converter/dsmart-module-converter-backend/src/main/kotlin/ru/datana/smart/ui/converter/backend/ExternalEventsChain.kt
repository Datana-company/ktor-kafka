package ru.datana.smart.ui.converter.backend

import codes.spectrum.konveyor.DefaultKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.konveyor
import ru.datana.smart.ui.converter.backend.common.ConverterChainSettings
import ru.datana.smart.ui.converter.backend.common.setSettings
import ru.datana.smart.ui.converter.backend.handlers.*
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus

/*
* ExternalEventsChain - цепочка обработки внешних событий.
* */
class ExternalEventsChain(
    var chainSettings: ConverterChainSettings
) {

    suspend fun exec(context: ConverterBeContext) {
        exec(context, DefaultKonveyorEnvironment)
    }

    suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        context.setSettings(chainSettings)
        konveyor.exec(
            context,
            env
        )
    }

    companion object {
        val konveyor = konveyor<ConverterBeContext> {

            timeout { 1000 }

            // если текущий идентификатор плавки пустой, то завершаем обработку внешних событий
            handler {
                onEnv { status == CorStatus.STARTED && meltInfo.id.isEmpty() }
                exec {
                    status = CorStatus.FINISHED
                }
            }

            +GetActiveEventHandler // из репозитория извлекается активное событие
            +SetStreamStatus // устанавливается статус содержания потока

            // конвейер создания внешнего события
            konveyor {
                on { status == CorStatus.STARTED && externalEvent.alertRuleId.isNotBlank() }
                +SetEventExecutionStatusHandler // задаётся статус выполнения у текущего события
                +SetEventInactiveStatusHandler // задаётся статус активности у текущего события
                +UpdateEventHandler // обновление текущего события
                +CreateExternalEventHandler // создание внешнего события
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

            +FinishHandler // обработчик завершения цепочки
        }
    }
}
