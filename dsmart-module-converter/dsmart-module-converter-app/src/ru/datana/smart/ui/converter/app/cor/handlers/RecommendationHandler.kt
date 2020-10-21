package ru.datana.smart.ui.converter.app.cor.handlers

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import ru.datana.smart.ui.converter.app.cor.context.ConverterBeContext
import ru.datana.smart.ui.converter.app.cor.context.CorError
import ru.datana.smart.ui.converter.app.cor.context.CorStatus
import ru.datana.smart.ui.converter.ws.models.WsDsmartRecommendation
import ru.datana.smart.ui.converter.ws.models.WsDsmartResponseRecommendation
import ru.datana.smart.ui.mlui.models.ConverterTransportMlUi
import java.time.Instant

object RecommendationHandler : IKonveyorHandler<ConverterBeContext<String, String>> {

    override suspend fun exec(context: ConverterBeContext<String, String>, env: IKonveyorEnvironment) {
        val record = context.records.firstOrNull { it.topic == context.topicConverter } ?: return

        context.logger.trace("topic = ${record.topic}, partition = ${record.partition}, offset = ${record.offset}, key = ${record.key}, value = ${record.value}")

        try {
            val obj = context.jacksonSerializer.readValue(record.value, ConverterTransportMlUi::class.java)!!

            val steelRate = obj.steelRate ?: return

            val response = when {
                steelRate >= 0.8 && context.recommendationTimer.isTimeOver("critical") -> {
                    context.recommendationTimer.start("critical")
                    WsDsmartResponseRecommendation(
                        data = WsDsmartRecommendation(
                            time = Instant.now().toEpochMilli(),
                            category = WsDsmartRecommendation.Category.CRITICAL,
                            textMessage = """
                            В потоке детектирован $steelRate металла, превышен допустимый предел % потери. Верните конвертер в вертикальное положение.
                        """.trimIndent()
                        )
                    )
                }
                steelRate > 0.05 && steelRate < 0.8 && context.recommendationTimer.isTimeOver("critical-warning") -> {
                    context.recommendationTimer.start("critical-warning")
                    WsDsmartResponseRecommendation(
                        data = WsDsmartRecommendation(
                            time = Instant.now().toEpochMilli(),
                            category = WsDsmartRecommendation.Category.WARNING,
                            textMessage = """
                            Достигнут предел металла $steelRate в потоке.
                        """.trimIndent()
                        )
                    )
                }
                steelRate == 0.05 && context.recommendationTimer.isTimeOver("warning") -> {
                    context.recommendationTimer.start("warning")
                    WsDsmartResponseRecommendation(
                        data = WsDsmartRecommendation(
                            time = Instant.now().toEpochMilli(),
                            category = WsDsmartRecommendation.Category.WARNING,
                            textMessage = """
                            Достигнут предел металла $steelRate в потоке.
                        """.trimIndent()
                        )
                    )
                }
                else -> {
                    return
                }
            }

            context.forwardObjects.add(response)

        } catch (e: Throwable) {
            val msg = "Error parsing data for [Proc]: ${record}"
            context.logger.error(msg)
            context.errors.add(CorError(msg))
            context.status = CorStatus.FAILING
        }
    }

    override fun match(context: ConverterBeContext<String, String>, env: IKonveyorEnvironment): Boolean {
        return context.status == CorStatus.STARTED
    }

}
