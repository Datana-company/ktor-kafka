package ru.datana.smart.ui.common.ktor.kafka

import com.typesafe.config.ConfigFactory
import io.ktor.config.HoconApplicationConfig
import io.ktor.util.KtorExperimentalAPI
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.junit.BeforeClass
import java.util.*
import kotlin.test.Test
import kotlin.test.BeforeTest
import io.ktor.server.testing.*
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.KafkaConsumer

internal class ExtEventsKafkaTest {

    @BeforeTest
    fun metaTestBefore() {
//        val kafkaServers: String by lazy { environment.config.property("ktor.kafka.bootstrap.servers").getString().trim() }
        val kafkaProducer: KafkaProducer<String, String> by lazy {
            val props = Properties().apply {
                put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "172.29.40.58:9092")
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
        val jsonString = """
                {
                    "alert-rule-id": "RULE:ID-ALERT-ON-INTO-RANGE-TEMPERATURE",
                    "container-id": "worker1.datana.ru",
                    "component": "adapter-socket",
                    "@timestamp": "2020-11-06T00:36:31.055Z",
                    "level": "INFO",
                    "logger_name": "ru.datana.integrationadapter.socket.integration.processors.SendInTransportProcessorImpl",
                    "message": "[Версия 6: Для Кафки] Температура в диапазоне 20..30 градусов "
                }""".trimIndent()
        kafkaProducer.send( ProducerRecord("dev-converter-events", jsonString) )
        kafkaProducer.send( ProducerRecord("dev-converter-events", jsonString) )
        kafkaProducer.send( ProducerRecord("dev-converter-events", jsonString) )
    }

//    private fun createConsumer(brokers: String): Consumer<String, String> {
//        val props = Properties()
//        props["bootstrap.servers"] = brokers
//        props["group.id"] = "person-processor"
//        props["key.deserializer"] = StringDeserializer::class.java
//        props["value.deserializer"] = StringDeserializer::class.java
//        return KafkaConsumer<String, String>(props)
//    }

//    @Test
//    fun testKafkaExtEvents() {
//        println(" +++ testKafkaExtEvents")
//        withTestApplication(Application::main){
////            main()
//        }
//    }

    @Test
    fun testRoot() {
        with(engine) {
//            handleRequest(HttpMethod.Get, "/").apply {
//                assertEquals(HttpStatusCode.OK, response.status())
//                assertTrue {
//                    response.content?.contains("Hello World!") ?: false
//                }
//            }
        }
    }

    companion object {
        @OptIn(KtorExperimentalAPI::class)
        private val engine = TestApplicationEngine(createTestEnvironment {
            config = HoconApplicationConfig(ConfigFactory.load("application.conf"))
        })

        @BeforeClass
        @JvmStatic fun setup(){
            engine.start(wait = false)
        }
    }
}
