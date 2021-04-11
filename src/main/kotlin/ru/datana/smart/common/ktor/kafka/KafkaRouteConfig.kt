package ru.datana.smart.common.ktor.kafka

import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.common.serialization.BytesDeserializer
import org.apache.kafka.common.serialization.StringDeserializer

class KafkaRouteConfig<K, V>(
    var pollInterval: Long = 60L,
    var brokers: String = "localhost:9092",
    var clientId: String = "",
    var groupId: String = "",
    var keyDeserializer: Class<*> = StringDeserializer::class.java,
    var valDeserializer: Class<*> = BytesDeserializer::class.java
) {
    private val _topicHandlers: MutableList<KtorKafkaHandler<K,V>> = mutableListOf()
    var consumer: Consumer<K, V>? = null

    fun topic(name: String, handler: suspend KtorKafkaConsumerContext<K,V>.() -> Unit) {
        _topicHandlers.add(
            KtorKafkaHandler(topic = name, handler = handler)
        )
    }
    fun topics(vararg names: String, handler: suspend KtorKafkaConsumerContext<K,V>.() -> Unit) {
        names.forEach { name ->
            topic(name, handler)
        }
    }

    val topicHandlers: List<KtorKafkaHandler<K,V>>
        get() = _topicHandlers.toList()
}

class KtorKafkaHandler<K,V>(
    val topic: String,
    val handler: suspend KtorKafkaConsumerContext<K,V>.() -> Unit
) {

}
