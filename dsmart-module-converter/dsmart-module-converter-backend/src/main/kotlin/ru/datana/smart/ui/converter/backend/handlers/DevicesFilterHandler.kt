package ru.datana.smart.ui.converter.backend.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.logger.datanaLogger
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus

/*
* DevicesFilterHandler - происходит фильтрация по идентификатору устройства converterId.
* Если текущий идентификатор устройства не совпадает с заданным в конфигурации,
* то дальше chain не занимается обработкой данных.
* */
object DevicesFilterHandler: IKonveyorHandler<ConverterBeContext> {
    val logger = datanaLogger(this::class.java)

    override suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        if (context.converterId != context.meltInfo.devices.converter.id) {
            context.status = CorStatus.FINISHED
            logger.debug(msg = "Operation is skopped due to CONVERTER_ID fileter: " +
                "${context.converterId} != ${context.meltInfo.devices.converter.id}")
        } else {
            logger.debug(msg = "Filter CONVERTER_ID is passed")

        }
    }

    override fun match(context: ConverterBeContext, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }
}
