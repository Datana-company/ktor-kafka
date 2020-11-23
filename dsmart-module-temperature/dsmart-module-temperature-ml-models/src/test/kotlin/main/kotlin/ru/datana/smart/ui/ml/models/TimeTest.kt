package main.kotlin.ru.datana.smart.ui.ml.models

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.ObjectMapper
import ru.datana.smart.ui.ml.models.TemperatureMlUiDto
import kotlin.test.Test
import java.time.Instant
import kotlin.test.Ignore
import kotlin.test.assertEquals

class TimeTest {

    @Ignore
    @Test
    fun inputV01Test() {
        val json = """
            {"version": "0.1", "boilTime": "2020-09-22T18:26:17.246000Z", "deviceState": "switchedOff"}
        """.trimIndent()

        val objectMapper = ObjectMapper().configure(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS, true)
        val obj = objectMapper.readValue(json, TemperatureMlUiDto::class.java)

        assertEquals("0.1", obj.version)
//        assertEquals(Instant.parse("2020-09-22T18:26:17.246Z"), Instant.parse(obj.boilTime))
//        assertEquals(TemperatureUI.DeviceState.SWITCHED_OFF, obj.deviceState)
    }

    @Test
    fun inputV02Test() {
        val json = """
            {"version": "0.2", "timeActual": 1601047090000, "durationToBoil": 124261, "sensorId": "8e630dd0-5796-45e0-8d85-8a14c5d872dd", "temperatureLast": "24.41999969482422", "state": "switchedOff"}
        """.trimIndent()

        val objectMapper = ObjectMapper().configure(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS, true)
        val obj = objectMapper.readValue(json, TemperatureMlUiDto::class.java)

        assertEquals("0.2", obj.version)
        assertEquals(Instant.ofEpochMilli(1601047090000L), obj.timeActual?.let {Instant.ofEpochMilli(it)})
        assertEquals(TemperatureMlUiDto.State.SWITCHED_OFF, obj.state)
        assertEquals("8e630dd0-5796-45e0-8d85-8a14c5d872dd", obj.sensorId)
        assertEquals(124261, obj.durationToBoil)
        assertEquals(24.41999969482422, obj.temperatureLast)
    }
}
