package ru.datana.smart.common.ktor.kafka

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.config.*
import io.ktor.routing.routing
import io.ktor.server.testing.*
import io.ktor.util.*
import io.ktor.util.collections.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.apache.kafka.clients.consumer.Consumer
import java.time.Duration
import kotlin.test.Test
import kotlin.test.assertTrue


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
fun Application.module(
    mockConsumer: Consumer<String, String>? = null,
    topic: String = "",
    feedBack: MutableList<String> = mutableListOf()
) {

    install(KtorKafkaConsumer) {
    }

    routing {
        kafka<String, String> {
            consumer = mockConsumer
            topic("ui-temperature") {
                items.items.forEach {
                    feedBack.add(it.value)
                }
            }
        }
    }
}

class TestKafkaApplication {
    @OptIn(KtorExperimentalAPI::class)
    @Test
    fun test() {
        val feedBack: MutableList<String> = ConcurrentList()
        val consumer: TestConsumer<String, String> = TestConsumer<String, String>(duration = Duration.ofMillis(20))
        withTestApplication({
            (environment.config as MapApplicationConfig).apply {
                put("xxx", "yyy")
            }
            module(
                mockConsumer = consumer,
                topic = TOPIC,
                feedBack = feedBack
            )
        }) {
            runBlocking {
                consumer.send(TOPIC, "xx1", "yy")
                consumer.send(TOPIC, "xx2", "zz")

                delay(30L)

                assertTrue("Must contain two messages") {
                    feedBack.contains("yy") && feedBack.contains("zz")
                }
            }
        }
    }

    companion object {
        const val TOPIC = "some-topic"
    }
}
