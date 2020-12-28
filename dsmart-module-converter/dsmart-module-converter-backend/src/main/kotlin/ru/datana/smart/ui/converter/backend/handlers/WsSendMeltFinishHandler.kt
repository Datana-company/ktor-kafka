package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.CurrentState
import ru.datana.smart.ui.converter.common.models.ModelEventMode
import ru.datana.smart.ui.converter.common.models.ModelMeltInfo

/*
* WsSendMeltFinishHandler - конец плавки.
* Если данные из видеопотока не проходили в течении заданного времени (MELT_TIMEOUT),
* то на фронтенд отправляются данные об окончании плавки и сбрасывается репозиторий.
* */
object WsSendMeltFinishHandler: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        val schedule = context.scheduleCleaner.get()
        with(schedule) {
            jobMeltFinish?.let {
                // если текущая джоба актива, то отменяем её выполнение
                if (it.isActive)
                    it.cancel()
            }
            // отправка пустых данных по истечению времени (MELT_TIMEOUT)
            jobMeltFinish = GlobalScope.launch {
                // происходит ожидание в течение заданного времени (MELT_TIMEOUT)
                delay(context.meltTimeout)
                // сбор контекста перед вызовом цепочек с обработкой событий и светофора
                context.meltInfo = ModelMeltInfo.NONE
                context.currentStateRepository.updateStreamRate(context.converterId, Double.MIN_VALUE)
                context.status = CorStatus.STARTED

                // вызов цепочки обработки событий по шлаку или по металлу
                if (context.eventMode == ModelEventMode.STEEL) {
                    context.converterFacade.handleSteelEvents(context)
                } else if (context.eventMode == ModelEventMode.SLAG) {
                    context.converterFacade.handleSlagEvents(context)
                }
                // вызов цепочки обработки светофора
                context.converterFacade.handleSignaler(context)
                // сброс данных в репозитории текущего состояния
                context.currentStateRepository.update(CurrentState.NONE)
                // отправка данных об окончании плавки
                context.wsManager.sendFinish(context)
                println("jobMeltFinish done")
            }
        }
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
