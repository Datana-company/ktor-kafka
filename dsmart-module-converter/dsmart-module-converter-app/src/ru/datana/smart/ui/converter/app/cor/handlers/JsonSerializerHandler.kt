package ru.datana.smart.ui.converter.app.cor.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.app.cor.context.CorError
import ru.datana.smart.ui.converter.app.cor.context.CorStatus
import ru.datana.smart.ui.converter.app.cor.context.ConverterBeContext
import ru.datana.smart.ui.converter.ws.models.WsDsmartResponseConverter

object JsonSerializerHandler : IKonveyorHandler<ConverterBeContext<String, String>> {

    override suspend fun exec(context: ConverterBeContext<String, String>, env: IKonveyorEnvironment) {
        context.forwardJsonObjects = context.forwardObjects.map {
            when(it) {
                is WsDsmartResponseConverter -> context.kotlinxSerializer.encodeToString(WsDsmartResponseConverter.serializer(), it)
                else -> {
                    val msg = "Unknown type of data"
                    context.logger.trace(msg)
                    context.errors.add(CorError(msg))
                    context.status = CorStatus.FAILING
                    return
                }
            }
        }
    }

    override fun match(context: ConverterBeContext<String, String>, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
            && context.forwardObjects.isNotEmpty()
    }

}
