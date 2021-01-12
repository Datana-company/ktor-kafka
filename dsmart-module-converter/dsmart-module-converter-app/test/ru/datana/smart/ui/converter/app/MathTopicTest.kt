package ru.datana.smart.ui.converter.app

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.protobuf.ByteString
import io.ktor.config.*
import io.ktor.http.cio.websocket.*
import io.ktor.server.testing.*
import io.ktor.util.*
import kotlinx.coroutines.withTimeout
import ru.datana.smart.common.ktor.kafka.TestConsumer
import ru.datana.smart.converter.transport.math.*
import ru.datana.smart.converter.transport.meta.models.*
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertTrue

internal class MathTopicTest {

    val objectmapper = ObjectMapper()
    val metaConsumer = TestConsumer<String, String>()
    val mathConsumer = TestConsumer<String, ByteArray>()

    val meltInit = ConverterMeltInfo(
        id = MELT_ID,
        timeStart = Instant.now().toEpochMilli(),
        meltNumber = "1234",
        steelGrade = "X12-34",
        crewNumber = "2",
        shiftNumber = "2",
        mode = ConverterWorkMode.EMULATION,
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

//    val meltInit = """
//        {
//           "id": "$MELT_ID",
//           "timeStart": ${Instant.now().toEpochMilli()},
//           "meltNumber": "111616",
//           "steelGrade": "X65ME",
//           "crewNumber": "1",
//           "shiftNumber": "1",
//           "mode": "emulation",
//           "devices": {
//              "converter": {
//                 "deviceType": "ConverterDevicesConverter",
//                 "id": "$CONVERTER_ID",
//                 "name": "Конвертер 1",
//                 "uri": null,
//                 "type": null
//              },
//              "irCamera": {
//                 "deviceType": "ConverterDevicesIrCamera",
//                 "id": "ir-cam-25",
//                 "name": "Камера 25",
//                 "uri": "case-Финал1/Финал1.ravi",
//                 "type": "file"
//              },
//              "selsyn": {
//                 "deviceType": "ConverterDevicesSelsyn",
//                 "id": "conv1-selsyn1",
//                 "name": "Angles mesurement",
//                 "uri": "case-Финал1/selsyn.json",
//                 "type": "file"
//              },
//              "slagRate": {
//                 "deviceType": "ConverterDevicesSlagRate",
//                 "id": "conv1-slagRate1",
//                 "name": "Slag and steel rates resolution",
//                 "uri": "case-Финал1/slag-rate.json",
//                 "type": "file"
//              }
//           }
//        }
//    """.trimIndent()

    val mathMessage = ConverterTransportMlUiOuterClass.ConverterTransportMlUi.newBuilder()
        .setMath(
            ConverterTransportMathOuterClass.ConverterTransportMath.newBuilder()
                .setAngle(0.0)
                .setSlagRate(0.88)
                .setSteelRate(0.12)
                .setFrame(ByteString.copyFrom(ByteArray(4) { it.toByte() }))
        )
        .setFrameId("frame-id-123")
        .setFrameTime(Instant.now().toEpochMilli())
        .setMeltInfo(
            ConverterMeltInfoOuterClass.ConverterMeltInfo.newBuilder()
                .setId(MELT_ID)
                .setCrewNumber("1")
                .setMeltNumber("12354123")
                .setShiftNumber("2")
                .setSteelGrade("X456-DF")
                .setDevices(
                    ConverterMeltDevicesOuterClass.ConverterMeltDevices.newBuilder()
                        .setConverter(
                            ConverterDevicesConverterOuterClass.ConverterDevicesConverter.newBuilder()
                                .setId(CONVERTER_ID)
                                .setDeviceType("ConverterDevicesConverter")
                                .setName("Our Converter")
                        )
                        .setIrCamera(
                            ConverterDevicesIrCameraOuterClass.ConverterDevicesIrCamera.newBuilder()
                                .setId("camera-id")
                                .setDeviceType("ConverterDevicesIrCamera")
                                .setName("Our Camera")
                        )
                )
        ).build()


    @OptIn(KtorExperimentalAPI::class)
    @Test
    fun testRequests() {
        withTestApplication({
            (environment.config as MapApplicationConfig).apply {
                put("ktor.kafka.consumer.topic.meta", TOPIC_META)
                put("ktor.kafka.consumer.topic.math", TOPIC_MATH)
                put("ktor.kafka.consumer.topic.angles", TOPIC_ANGLES)
                put("ktor.kafka.consumer.topic.events", TOPIC_EVENTS)
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

                // 2) Запускаем плавку.
                metaConsumer.send(TOPIC_META, "", objectmapper.writeValueAsString(meltInit))
//                metaConsumer.send(TOPIC_META, "", meltInit)
                withTimeout(3001) {
                    repeat(1) {
                        val meltInitMsg = (incoming.receive() as Frame.Text).readText()
                        println(" +++ meltInitMsg[$it]: $meltInitMsg")
                    }
                }

                // 3) Отправляем сообщение от матмодели
                mathConsumer.send(TOPIC_MATH, "1", mathMessage.toByteArray())
                withTimeout(3002) {
                    var json = ""
                    repeat(2) {
                        val jsonMath = (incoming.receive() as Frame.Text).readText()
                        println(" +++ mathJson: $jsonMath")
                        json += jsonMath
                    }
                    assertTrue(json.contains("\"image\":\"AAECAw==\""), "Image must be sent")
                    assertTrue(json.contains("\"steelRate\":0.12"), "Steel rate must be handled")
                }
            }
        }
    }

    companion object {
        const val TOPIC_META = "topic-meta"
        const val TOPIC_MATH = "topic-math"
        const val TOPIC_ANGLES = "topic-other"
        const val TOPIC_EVENTS = "topic-other"
        const val CONVERTER_ID = "converter-xx"
        const val MELT_ID = "melt-id-345"
    }
}
