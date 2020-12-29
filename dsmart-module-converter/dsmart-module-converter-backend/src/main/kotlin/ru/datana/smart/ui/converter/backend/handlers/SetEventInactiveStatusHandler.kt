package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.ModelEvent
import ru.datana.smart.ui.converter.common.models.ModelEvent.Category
import ru.datana.smart.ui.converter.common.models.ModelEvent.ExecutionStatus
import ru.datana.smart.ui.converter.common.models.ModelStreamStatus

/*
* SetEventInactiveStatusHandler - записывает текущее событие в историю.
* */
object SetEventInactiveStatusHandler: IKonveyorHandler<ConverterBeContext> {
    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        // сначала определяется, какая категория события является актульаной,
        // это зависит от статуса потока
        val actualEventCategory = when (context.streamStatus) {
            ModelStreamStatus.CRITICAL -> Category.CRITICAL
            ModelStreamStatus.WARNING -> Category.WARNING
            ModelStreamStatus.INFO -> Category.INFO
            ModelStreamStatus.END -> Category.NONE
            ModelStreamStatus.NORMAL -> Category.NONE
            ModelStreamStatus.NONE -> Category.NONE
        }
        with(context.activeEvent) {
            // в историю отправляем событие, если оно неактуальной категории, или у него статус выполнения
            if (this == ModelEvent.NONE || (category == actualEventCategory
                    && executionStatus != ExecutionStatus.COMPLETED
                    && executionStatus != ExecutionStatus.FAILED))
                return

            isActive = false
        }
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
