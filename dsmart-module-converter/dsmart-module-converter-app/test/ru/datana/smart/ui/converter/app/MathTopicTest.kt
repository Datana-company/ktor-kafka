package ru.datana.smart.ui.converter.app

import com.fasterxml.jackson.databind.ObjectMapper
import com.typesafe.config.ConfigFactory
import io.ktor.application.*
import io.ktor.client.tests.utils.*
import io.ktor.config.*
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import io.ktor.server.testing.*
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.withTimeout
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.junit.BeforeClass
import org.junit.Ignore
import ru.datana.smart.common.ktor.kafka.KtorKafkaConsumer
import ru.datana.smart.common.ktor.kafka.TestConsumer
import ru.datana.smart.converter.transport.meta.models.*
import java.util.*
import kotlin.test.Test
import kotlin.test.assertTrue
import ru.datana.smart.ui.converter.app.module

internal class MathTopicTest {

    val objectmapper = ObjectMapper()
    val metaConsumer = TestConsumer<String, String>()
    val mathConsumer = TestConsumer<String, ByteArray>()

    val meltInit = ConverterMeltInfo(
        id = "converter1-2019328013-1608140222293",
        devices = ConverterMeltDevices(
            converter = ConverterDevicesConverter(
                id = CONVERTER_ID,
                name = "ConverterName"
            ),
            irCamera = ConverterDevicesIrCamera(
                id = "cameraXX",
                name = "Camera XX"
            )
        )
    )

    @OptIn(KtorExperimentalAPI::class)
    @Test
    fun testRequests() {
        withTestApplication({
            (environment.config as MapApplicationConfig).apply {
                put("ktor.kafka.consumer.topic.meta", TOPIC_META)
                put("ktor.kafka.consumer.topic.math", TOPIC_MATH)
                put("ktor.kafka.consumer.topic.angles", "")
                put("ktor.kafka.consumer.topic.events", "")
                put("ktor.datana.converter.id", CONVERTER_ID)
                put("ktor.conveyor.streamRatePoint.warning", "0.12")
                put("ktor.conveyor.streamRatePoint.critical", "0.20")
                put("ktor.conveyor.roundingWeight", "0.10")
            }
            module(
                testing = true,
                kafkaMetaConsumer = metaConsumer,
                kafkaMathConsumer = mathConsumer
            )
        }) {
            handleWebSocketConversation("/ws") { incoming, outgoing ->
                // 1) Считываем из Кафки первые пустые (инициализационные) сообщения
                repeat(2) {
                    val kafkaInitMsg = (incoming.receive() as Frame.Text).readText()
                    println(" +++ kafkaInitMsg: $kafkaInitMsg")
                }

                // 2) Запускаем плавку. Это нужно чтобы у extEvent-ов была принадлежность к плавке (проставлен meltId).
                metaConsumer.send(TOPIC_META, "", objectmapper.writeValueAsString(meltInit))
                withTimeout(3001) {
                    val meltInitMsg = (incoming.receive() as Frame.Text).readText()
                    println(" +++ meltInitMsg: $meltInitMsg")
                }

                // 3) Отправляем собственно тестовое сообщение (extEvent)
//            kafkaProducer.send(ProducerRecord("gitlab-converter-events", jsonString))
//            withTimeout(3002) {
//                val actualJson = (incoming.receive() as Frame.Text).readText()
//                println(" +++ actualJson: $actualJson")
//                assertTrue(actualJson.contains(testMsg))
//            }
            }
        }
    }

    companion object {
        const val TOPIC_META = "topic-meta"
        const val TOPIC_MATH = "topic-math"
        const val CONVERTER_ID = "converter-xx"
    }
}
