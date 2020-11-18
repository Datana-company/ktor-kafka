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

object WsSendMathFrameHandler: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        context.wsManager.sendFrames(context)

        val schedule = context.scheduleCleaner.get()
        with(schedule) {
            jobFrameMath?.let {
                if (it.isActive) {
                    it.cancel()
                    println("cancel jobFrameMath")
                }
            }
            jobFrameMath = GlobalScope.launch {
                delay(context.dataTimeout)
                context.frame = ModelFrame(channel = ModelFrame.Channels.MATH)
                context.wsManager.sendFrames(context)
                println("jobFrameMath done")
            }
        }
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
