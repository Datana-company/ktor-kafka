package ru.datana.smart.ui.converter.angle.app

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.application.*
import io.ktor.routing.routing
import io.ktor.util.*
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import ru.datana.smart.common.ktor.kafka.KtorKafkaConsumer
import ru.datana.smart.common.ktor.kafka.kafka
import ru.datana.smart.ui.converter.angle.app.mappings.toInnerModel
import ru.datana.smart.ui.converter.angle.app.models.AngleSchedule
import ru.datana.smart.ui.mlui.models.ConverterTransportMlUi
import ru.datana.smart.ui.mlui.models.ConverterMeltInfo
import ru.datana.smart.ui.mlui.models.ConverterTransportAngle
import java.io.File
import java.util.*
import kotlin.math.abs

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
@kotlin.jvm.JvmOverloads
@KtorExperimentalAPI
fun Application.module(testing: Boolean = false) {

    install(KtorKafkaConsumer) {
    }

    val scheduleBasePath by lazy { environment.config.property("paths.schedule.base ").getString().trim() }
    val topicMeta by lazy { environment.config.property("ktor.kafka.consumer.topic.meta").getString().trim() }
    val topicMath by lazy { environment.config.property("ktor.kafka.consumer.topic.math").getString().trim() }
    var angleSchedule: AngleSchedule? = null
    val kafkaServers: String by lazy {
        environment.config.property("ktor.kafka.bootstrap.servers").getString().trim()
    }
    val topicAngle: String by lazy {
        environment.config.property("ktor.kafka.producer.topic.angle").getString().trim()
    }
    val converterId by lazy { environment.config.property("ktor.datana.converter.id").getString().trim() }
    val jacksonSerializer: ObjectMapper = ObjectMapper().configure(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS, true)
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

    routing {

        kafka(listOf(topicMeta, topicMath)) {
            records.sortedByDescending { it.offset() }
//                на самом деле они уже отсортированы сначала по топику, затем по offset по убыванию
                .distinctBy { it.topic() }
                .map { it.toInnerModel() }
                .forEach { record ->
                    when (record.topic) {
                        topicMath -> {
                            println("-------- MATH")
                            val mlui = jacksonSerializer.readValue(
                                record.value,
                                ConverterTransportMlUi::class.java
                            )
                            if (converterId == mlui.meltInfo?.devices?.converter?.id
                                && angleSchedule != null
                                && mlui.frameTime != null
                                && mlui.meltInfo?.timeStart != null
                            ) {
                                val timeShift = mlui.frameTime!! - mlui.meltInfo?.timeStart!!
                                val closestMessage = angleSchedule?.items?.minByOrNull {
                                    abs(it.timeShift?.let { ts -> ts - timeShift } ?: Long.MAX_VALUE)
                                }
                                println(closestMessage)
                                val converterTransportAngle = ConverterTransportAngle(
                                    meltInfo = mlui.meltInfo,
                                    angleTime = mlui.frameTime,
                                    angle = closestMessage?.angle
                                )
                                val key = "${mlui.meltInfo?.timeStart}-${closestMessage?.timeShift}"
                                val json = jacksonSerializer.writeValueAsString(converterTransportAngle)
                                val sendingRecord = ProducerRecord(topicAngle, key, json)
                                kafkaProducer.send(sendingRecord)
                            }
                        }
                        topicMeta -> {
                            println("---- META")
                            val metaInfo = jacksonSerializer.readValue(
                                record.value,
                                ConverterMeltInfo::class.java
                            )
                            if (converterId == metaInfo.devices?.converter?.id) {
                                val scheduleRelativePath = metaInfo?.devices?.selsyn?.uri
                                val scheduleAbsolutePath = "${scheduleBasePath}/${scheduleRelativePath}"

                                val json = File(scheduleAbsolutePath).readText(Charsets.UTF_8)
                                angleSchedule = jacksonSerializer.readValue(
                                    json,
                                    AngleSchedule::class.java
                                )
                            }
                        }
                    }
                }
            commitAll()
        }
    }
}
