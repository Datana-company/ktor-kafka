package ru.datana.smart.common.ktor.kafka

import org.apache.kafka.common.serialization.Serializer

class KafkaSerializer<T>(): Serializer<T> {
    override fun serialize(topic: String?, data: T?): ByteArray? = null
}

//class JacksonSerializer<T>(private val objectMapper: ObjectMapper = ObjectMapper().registerModule(JavaTimeModule())) :
//    Serializer<T> {
//    override fun serialize(topic: String?, data: T?): ByteArray? {
//        return if (data == null) {
//            null
//        } else try {
//            objectMapper.writeValueAsBytes(data)
//        } catch (e: JsonProcessingException) {
//            throw SerializationException("Error serializing JSON message", e)
//        }
//    }
//}
