package ru.datana.smart.ui.converter.app

import com.typesafe.config.ConfigFactory
import io.ktor.config.HoconApplicationConfig
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.createTestEnvironment
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.withTimeout
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.junit.BeforeClass
import java.util.*
import kotlin.test.Test
import kotlin.test.assertTrue

internal class ExtEventsKafkaTest {

    // Сообщение для инициализации плавки
    val meltInitJson = """
        {
          "id": "converter1-2019328013-1608140222293",
          "devices": {
            "converter": {
              "deviceType": "ConverterDevicesConverter",
              "id": "converter1"
            }
          }
        }""".trimIndent()

    val testMsg = "Тестовое сообщение 12345"
    val jsonString = """
                {
                    "alert-rule-id": "RULE:ID-ALERT-ON-INTO-RANGE-TEMPERATURE",
                    "container-id": "worker1.datana.ru",
                    "component": "adapter-socket",
                    "@timestamp": "2020-11-06T00:36:31.055Z",
                    "level": "INFO",
                    "logger_name": "ru.datana.integrationadapter.socket.integration.processors.SendInTransportProcessorImpl",
                    "message": "$testMsg"
                }""".trimIndent()

    @Test
    fun testConversation() {
        with(engine) {
            val kafkaServers: String by lazy {
                environment.config.property("ktor.kafka.bootstrap.servers").getString().trim()
            }
            val kafkaProducer: KafkaProducer<String, String> by lazy {
                val props = Properties().apply {
                    put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServers)
                    put("acks", "all")
                    put("retries", 3)
                    put("batch.size", 16384)
                    put("linger.ms", 1)
                    put("buffer.memory", 33554432)
                    put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
                    put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
                }
                KafkaProducer<String, String>(props)
            }

            handleWebSocketConversation("/ws") { incoming, outgoing ->
                // 1) Считываем из Кафки первые пустые (инициализационные) сообщения
                repeat(2) {
                    val kafkaInitMsg = (incoming.receive() as Frame.Text).readText()
                    println(" +++ kafkaInitMsg: $kafkaInitMsg")
                }
                // 2) Запускаем плавку. Это нужно чтобы у extEvent-ов была принадлежность к плавке (проставлен meltId).
                kafkaProducer.send(ProducerRecord("gitlab-converter-meta", meltInitJson))
                withTimeout(3000) {
                    val meltInitMsg = (incoming.receive() as Frame.Text).readText()
                    println(" +++ meltInitMsg: $meltInitMsg")
                }
                // 3) Отправляем собственно тестовое сообщение (extEvent)
                kafkaProducer.send(ProducerRecord("gitlab-converter-events", jsonString))
                withTimeout(3000) {
                    val actualJson = (incoming.receive() as Frame.Text).readText()
                    println(" +++ actualJson: $actualJson")
                    assertTrue(actualJson.contains(testMsg))
                }
            }
        }
    }

    companion object {
        @OptIn(KtorExperimentalAPI::class)
        private val engine = TestApplicationEngine(
            createTestEnvironment {
                config = HoconApplicationConfig(ConfigFactory.load("application-test.conf"))
            })

        @BeforeClass
        @JvmStatic
        fun setup() {
            engine.start(wait = false)
        }
    }
}
