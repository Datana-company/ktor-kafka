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

object WsSendFrameHandler: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        context.wsManager.sendFrames(context)

        val schedule = context.scheduleCleaner.get() ?: ScheduleCleaner()
        with(schedule) {
            jobFrameCamera?.let {
                if (it.isActive) {
                    it.cancel()
                    println("cancel jobFrameCamera")
                }
            }
            jobFrameCamera = GlobalScope.launch {
                delay(context.dataTimeout)
                context.frame = ModelFrame(channel = ModelFrame.Channels.CAMERA)
                context.wsManager.sendFrames(context)
                println("jobFrameCamera done")
            }
        }
        context.scheduleCleaner.set(schedule)
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
