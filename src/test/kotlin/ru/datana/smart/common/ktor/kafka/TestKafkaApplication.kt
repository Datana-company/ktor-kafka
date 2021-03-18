package ru.datana.smart.common.ktor.kafka

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.config.*
import io.ktor.routing.routing
import io.ktor.server.testing.*
import io.ktor.util.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import java.time.Duration
import kotlin.test.Test
import kotlin.test.assertTrue


@Suppress("unused") // Referenced in application.conf
fun Application.module(
    mockConsumer: Consumer<String, String>? = null,
    mockProducer: Producer<String, String>? = null,
    topicsIn: List<String> = listOf(),
    topicOut: String = "",
) {

    install(KtorKafkaConsumer) {
    }

    routing {
        kafka<String, String> {
            keyDeserializer = StringDeserializer::class.java
            valDeserializer = StringDeserializer::class.java
            consumer = mockConsumer
            pollInterval = 15L
            topics(*topicsIn.toTypedArray()) {
                items.items.forEach {
                    mockProducer?.send(
                        ProducerRecord(
                            topicOut, "Result: ${it.value}"
                        )
                    )
                }
            }
        }
    }
}

class TestKafkaApplication {
    @OptIn(KtorExperimentalAPI::class)
    @Test
    fun test() {
        val consumer: TestConsumer<String, String> = TestConsumer(duration = Duration.ofMillis(20))
        val producer: TestProducer<String, String> = TestProducer()
        withTestApplication({
            (environment.config as MapApplicationConfig).apply {
                put("xxx", "yyy")
            }
            module(
                mockConsumer = consumer,
                mockProducer = producer,
                topicsIn = listOf(TOPIC_IN),
                topicOut = TOPIC_OUT,
            )
        }) {
            runBlocking {
                delay(60L)
                consumer.send(TOPIC_IN, "xx1", "yy")
                consumer.send(TOPIC_IN, "xx2", "zz")

                delay(30L)

                assertTrue("Must contain two messages") {
                    val feedBack = producer.getSent().map { it.value() }
                    feedBack.contains("Result: yy") && feedBack.contains("Result: zz")
                }
            }
        }
    }

    @KtorExperimentalAPI
    @Test
    fun testTopics() {
        val topics = listOf("topic-1", "topic-2")
        val consumer: TestConsumer<String, String> = TestConsumer(duration = Duration.ofMillis(20))
        val producer: TestProducer<String, String> = TestProducer()
        withTestApplication({
            (environment.config as MapApplicationConfig).apply {
                put("xxx", "yyy")
            }
            module(
                mockConsumer = consumer,
                mockProducer = producer,
                topicsIn = topics,
                topicOut = TOPIC_OUT
            )
        }) {
            runBlocking {
                delay(50L)
                consumer.send(topics.first(), "testKey", "testBody")
                delay(30L)
                assertTrue("Must contain one message") {
                    val feedBack = producer.getSent().map { it.value() }
                    feedBack.contains("Result: testBody") && feedBack.size == 1
                }
            }
        }
    }

    companion object {
        const val TOPIC_IN = "some-topic-in"
        const val TOPIC_OUT = "some-topic-out"
    }
}
