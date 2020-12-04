package ru.datana.smart.ui.converter.mock.app

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import ru.datana.smart.logger.datanaLogger
import ru.datana.smart.ui.meta.models.*
import java.io.File
import java.time.Instant

class ConverterMockStartService(
    val pathToCatalog: String = "",
    val kafkaProducer: KafkaProducer<String, String>,
    val kafkaTopic: String = ""
) {

    private val logger = datanaLogger(this::class.java)
    private val objectMapper: ObjectMapper = jacksonObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);

    fun exec(context: ConverterMockContext) {
        logger.info(" +++ GET /send")
        val timeStart = Instant.now().toEpochMilli()
        logger.info(msg = "Starting new mock melt for case {}", objs = arrayOf(context.startCase))

        val meltInfo = try {
            val metaText = File("$pathToCatalog/${context.startCase}/meta.json").readText()
            objectMapper.readValue(metaText)
        } catch (e: Throwable) {
            EMPTY_START
        }
        val meltId = "${meltInfo.devices?.converter?.id ?: "unknown"}-${meltInfo.meltNumber}-$timeStart"
        val meltInfoInit = meltInfo.copy(
            id = meltId,
            timeStart = timeStart
        )
        val readyJsonString = objectMapper.writeValueAsString(meltInfoInit)
        try {
            kafkaProducer.send(ProducerRecord(kafkaTopic, meltId, readyJsonString))
            logger.biz(
                msg = "Send event is caught by converter-mock backend and successfully handled",
                data = object {
                    val id = "datana-smart-converter-mock-send-done"
                    val meltInfo = meltInfoInit
                }
            )
            context.status = ConverterMockContext.Statuses.OK
        } catch (e: Throwable) {
            logger.error("Send event failed due to kafka producer {}", objs = arrayOf(e))
            context.status = ConverterMockContext.Statuses.ERROR
        }
    }

    companion object {
        val EMPTY_START = ConverterMeltInfo(
            meltNumber = "unknown",
            steelGrade = "unknown",
            crewNumber = "0",
            shiftNumber = "-1",
            mode = ConverterMeltInfo.Mode.EMULATION,
            devices = ConverterMeltDevices(
                converter = ConverterDevicesConverter(
                    id = "converterUnk",
                    name = "Неизвестный конвертер",
                ),
                irCamera = ConverterDevicesIrCamera(
                    id = "converterUnk-camera",
                    name = "Неизвестная камера",
                    type = ConverterDeviceType.FILE,
                    uri = "case-case1/file.ravi"
                ),
                selsyn = ConverterDevicesSelsyn(
                    id = "converterUnk-selsyn",
                    name = "Неизвестный селсин",
                    type = ConverterDeviceType.FILE,
                    uri = "case-case1/selsyn.json"
                ),
                slagRate = ConverterDevicesSlagRate(
                    id = "converterUnk-composition",
                    name = "Неизвестный селсин",
                    type = ConverterDeviceType.FILE,
                    uri = "case-case1/slag-rate.json"
                )
            )
        )
    }
}
