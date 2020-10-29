package ru.datana.smart.ui.converter.angle.app.cor.services

import codes.spectrum.konveyor.DefaultKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.konveyor
import org.apache.kafka.clients.producer.KafkaProducer
import ru.datana.smart.logger.DatanaLogContext
import ru.datana.smart.ui.converter.angle.app.cor.context.ConverterAngleContext
import ru.datana.smart.ui.converter.angle.app.cor.handlers.ConverterMetaHandler
import ru.datana.smart.ui.converter.angle.app.cor.handlers.DataExtractorHandler

class ForwardServiceKafka(
    val logger: DatanaLogContext,
    val anglesBasePath: String,
    val topicAngles: String,
    val kafkaProducer: KafkaProducer<String, String>
) {

    suspend fun exec(context: ConverterAngleContext<String, String>) {
        exec(context, DefaultKonveyorEnvironment)
    }

    suspend fun exec(context: ConverterAngleContext<String, String>, env: IKonveyorEnvironment) {
        konveyor.exec(
            context.also {
                it.logger = logger
                it.anglesBasePath = anglesBasePath
                it.topicAngles = topicAngles
                it.kafkaProducer = kafkaProducer
            },
            env
        )
    }

    companion object {
        val konveyor = konveyor<ConverterAngleContext<String, String>> {

            timeout { 1000 }

            +ConverterMetaHandler
            +DataExtractorHandler
        }
    }
}
