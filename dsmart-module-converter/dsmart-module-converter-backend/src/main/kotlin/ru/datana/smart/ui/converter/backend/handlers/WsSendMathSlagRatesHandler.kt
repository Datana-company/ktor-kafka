package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus

/*
* WsSendMathSlagRatesHandler - происходит отправка данных о содержании потока на фронтенд через web-socket.
* Если данные о содержании потока не проходили в течении заданного времени (DATA_TIMEOUT),
* то на фронтенд отправляются пустые значения.
* */
object WsSendMathSlagRatesHandler: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        // отправка данных о содержании потока по web-socket
        context.wsManager.sendSlagRates(context)

        val schedule = context.scheduleCleaner.get()
        with(schedule) {
            jobSlagRate?.let {
                // если текущая джоба актива, то отменяем её выполнение
                if (it.isActive)
                    it.cancel()
            }
            // отправка пустых данных о содержании потока по истечению времени (DATA_TIMEOUT)
            jobSlagRate = GlobalScope.launch {
                // происходит ожидание в течение заданного времени (DATA_TIMEOUT)
                delay(context.dataTimeout)
                // лист данных о содержании потока в контексте заполняется значением по умолчанию
                context.slagRateList = mutableListOf()

                // задаётся текущее содержание потока в репозиторий текущего состояния
                //context.currentStateRepository.updateSlagRate(null, context.slagRate)
                context.currentStateRepository.addSlagRate(context.slagRate)

                // отправка пустых данных о содержании потока по web-socket
                context.wsManager.sendSlagRates(context)
                println("jobMath done")
            }
        }

        // задаётся текущее содержание потока в репозиторий текущего состояния
        //context.currentStateRepository.updateSlagRate(null, context.slagRate)
        context.currentStateRepository.addSlagRate(context.slagRate)
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
