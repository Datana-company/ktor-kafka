# KtorKafka

This is a Kafka feature for KTOR.

## Install

[![](https://jitpack.io/v/Datana-company/ktor-kafka.svg)](https://jitpack.io/#Datana-company/ktor-kafka)

```kotlin
    repositories {
        maven { url = uri("https://jitpack.io") }
    }

    dependencies {
        implementation("com.github.Datana-company:ktor-kafka:$ktorKafkaVersion")
        implementation("org.apache.kafka:kafka-clients:$kafkaVersion")
    }
```

## Usage
```kotlin
fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
fun Application.module(
    testConsumer: Consumer<String, String>? = null,
    testProducer: Producer<String, String>? = null,
) {
    val topicIn by lazy { environment.config.property("kafka.topicIn").getString() }
    val topicOut by lazy { environment.config.property("kafka.topicOut").getString() }
    val producer: Producer<String, String> = testProducer ?: run {
        KafkaProducer(
            mapOf(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to brokers,
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            )
        )
    }

    install(KtorKafkaConsumer)

    kafka<String, String> {
        keyDeserializer = StringDeserializer::class.java // Do not forget
        valDeserializer = StringDeserializer::class.java // to correctly provide them

        consumer = testConsumer // If null, it will be correctly generated.
                                 // Main purpose is to provide testing instances

        topic(topicIn) {
            for (item in items.items) {
                println("GOT message: topic=${item.topic}, key=${item.key}, body=${item.value}")
                kafkaProducer.send(
                    ProducerRecord(
                        topicOut,
                        "Hello World",
                    )
                )
            }
        }
    }
}
```

## Testing

```kotlin
class TestKafkaApplication {
    @OptIn(KtorExperimentalAPI::class)
    @Test
    fun test() {

        // create testing instances for the consumer and producer
        val consumer: TestConsumer<String, String> = TestConsumer(duration = Duration.ofMillis(20))
        val producer: TestProducer<String, String> = TestProducer()

        withTestApplication({
            (environment.config as MapApplicationConfig).apply {
                put("kafka.topicIn", TOPIC_IN)
                put("kafka.topicOut", TOPIC_OT)
                put("kafka.brokers", BROKERS)
            }
            module(
                testConsumer = consumer,
                testProducer = producer,
            )
        }) {
            runBlocking {
                delay(60L)
                consumer.send(TOPIC_IN, "xx1", "yy")

                delay(30L)

                assertTrue("Must a message") {
                    val feedBack = producer.getSent().map { it.value() }
                    feedBack.contains("Hello World")
                }
            }
        }
    }

    companion object {
        const val TOPIC_IN = "some-topic-in"
        const val TOPIC_OT = "some-topic-ot"
    }
}
```

## Contributors

1. [Datana](https://datana.ru)
