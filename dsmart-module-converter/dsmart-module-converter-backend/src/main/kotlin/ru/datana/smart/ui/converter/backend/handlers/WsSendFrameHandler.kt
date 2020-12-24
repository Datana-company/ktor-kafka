package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.ModelFrame
import ru.datana.smart.ui.converter.common.models.ScheduleCleaner

/*
* WsSendFrameHandler - происходит отправка данных о кадрам из видеоадаптера на фронтенд через web-socket.
* Если данные о кадрам не проходили в течении заданного времени (DATA_TIMEOUT),
* то на фронтенд отправляются пустые значения.
* */
object WsSendFrameHandler: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        // отправка данных о кадре из видеоадаптера по web-socket
        context.wsManager.sendFrames(context)

        val schedule = context.scheduleCleaner.get()
        with(schedule) {
            jobFrameCamera?.let {
                // если текущая джоба актива, то отменяем её выполнение
                if (it.isActive)
                    it.cancel()
            }
            // отправка пустых данных о кадре из видеоадаптера по истечению времени (DATA_TIMEOUT)
            jobFrameCamera = GlobalScope.launch {
                // происходит ожидание в течение заданного времени (DATA_TIMEOUT)
                delay(context.dataTimeout)
                // модель кадров в контексте заполняется значением по умолчанию
                context.frame = ModelFrame(channel = ModelFrame.Channels.CAMERA)
                // отправка пустых данных о кадре из видеоадаптера по web-socket
                context.wsManager.sendFrames(context)
                println("jobFrameCamera done")
            }
        }
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
