package ru.datana.smart.ui.converter.angle.app

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.protobuf.ByteString
import io.ktor.config.*
import io.ktor.server.testing.*
import io.ktor.util.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Test
import ru.datana.smart.common.ktor.kafka.TestConsumer
import ru.datana.smart.common.ktor.kafka.TestProducer
import ru.datana.smart.converter.transport.math.*
import ru.datana.smart.converter.transport.meta.models.*
import ru.datana.smart.converter.transport.mlui.models.ConverterTransportAngle
import ru.datana.smart.ui.converter.angle.app.mappings.jacksonSerializer
import java.time.Duration
import java.time.Instant
import kotlin.test.assertEquals

class IntegrationTest {

    val objectmapper = ObjectMapper()
    val metaConsumer = TestConsumer<String, String>(duration = Duration.ofMillis(20))
    val mathConsumer = TestConsumer<String, ByteArray>(duration = Duration.ofMillis(20))
    val anglesProducer = TestProducer<String, String>()

    val currentTime = Instant.now()
    val frameTime = currentTime.toEpochMilli()
    val meltTime = currentTime.minusMillis(9563).toEpochMilli()

    val meltInit = ConverterMeltInfo(
        id = MELT_ID,
        timeStart = meltTime,
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
            ),
            selsyn = ConverterDevicesSelsyn(
                id = "selsynYY",
                name = "test selsyn",
                uri = "test-1.json",
                deviceType = ConverterDevicesSelsyn::class.java.simpleName,
                type = ConverterDeviceType.FILE
            )
        )
    )

    val mathMessage = ConverterTransportMlUiOuterClass.ConverterTransportMlUi.newBuilder()
        .setMath(
            ConverterTransportMathOuterClass.ConverterTransportMath.newBuilder()
                .setAngle(0.0)
                .setSlagRate(0.88)
                .setSteelRate(0.12)
                .setFrame(ByteString.copyFrom(ByteArray(4) { it.toByte() }))
        )
        .setFrameId("frame-id-123")
        .setFrameTime(frameTime)
        .setMeltInfo(
            ConverterMeltInfoOuterClass.ConverterMeltInfo.newBuilder()
                .setId(MELT_ID)
                .setTimeStart(meltTime)
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
                        .setSelsyn(
                            ConverterDevicesSelsynOuterClass.ConverterDevicesSelsyn.newBuilder()
                                .setId("selsynYY")
                                .setDeviceType("ConverterDevicesSelsyn")
                                .setName("out test selsyn")
                                .setUri("test-1.json")
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
                put("ktor.kafka.producer.topic.angle", TOPIC_ANGLES)
                put("paths.schedule.base", BASE_PATH)
                put("ktor.datana.converter.id", CONVERTER_ID)
//                put("ktor.conveyor.streamRatePoint.warning", "0.12")
//                put("ktor.conveyor.streamRatePoint.critical", "0.20")
//                put("ktor.conveyor.roundingWeight", "0.10")
            }
            module(
                testing = true,
                kafkaMetaConsumer = metaConsumer,
                kafkaMathConsumer = mathConsumer,
                kafkaAnglesProducer = anglesProducer
            )
        }) {

            runBlocking {
                // 1) Запускаем плавку.
                metaConsumer.send(TOPIC_META, "", objectmapper.writeValueAsString(meltInit))
                delay(100L)

                // 3) Отправляем сообщение от матмодели
                mathConsumer.send(TOPIC_MATH, "1", mathMessage.toByteArray())
                delay(102)
                val messages = anglesProducer.getSent()
                assertEquals(1, messages.size)
                val message = messages[0]
                val angleMessage: ConverterTransportAngle = jacksonSerializer
                    .readValue(message.value(), ConverterTransportAngle::class.java)
                assertEquals(34.0, angleMessage.angle)
            }
        }
    }

    companion object {
        const val CONVERTER_ID = "conv-id-123"
        const val MELT_ID = "conv-id-123-melt-345"

        const val TOPIC_META = "converter-meta"
        const val TOPIC_MATH = "converter-math"
        const val TOPIC_ANGLES = "converter-angles"

        const val BASE_PATH = "testresources/base-path"
    }
}
