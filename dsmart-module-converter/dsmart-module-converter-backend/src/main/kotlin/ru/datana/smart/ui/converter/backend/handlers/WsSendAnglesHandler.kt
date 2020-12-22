package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.*

/*
* WsSendAnglesHandler - происходит отправка данных об углах на фронтенд через web-socket.
* Если данные об углам не проходили в течении заданного времени (DATA_TIMEOUT),
* то на фронтенд отправляются пустые данные.
* */
object WsSendAnglesHandler: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        // отправка угла по web-socket
        context.wsManager.sendAngles(context)

        val schedule = context.scheduleCleaner.get()
        with(schedule) {
            jobAngles?.let {
                // если текущая джоба актива, то отменяется её выполнение
                if (it.isActive)
                    it.cancel()
            }
            // отправка пустых данных об угле по истечению времени (DATA_TIMEOUT)
            jobAngles = GlobalScope.launch {
                // происходит ожидание в течение заданного времени (DATA_TIMEOUT)
                delay(context.dataTimeout)
                // угол в контексте заполняется значением по умолчанию
                context.angles = ModelAngles.NONE

                // задаётся текущее значение угла в репозиторий текущего состояния
                val curState = context.currentState.get()
                curState.lastAngles = context.angles

                // отправка пустых данных об угле по web-socket
                context.wsManager.sendAngles(context)
                println("jobAngles done")
            }
        }

        // задаём текущее значение угла в репозиторий текущего состояния
        val curState = context.currentState.get()
        curState.lastAngles = context.angles
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
