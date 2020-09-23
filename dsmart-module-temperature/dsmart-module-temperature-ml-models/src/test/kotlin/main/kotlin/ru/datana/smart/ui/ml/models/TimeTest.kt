package main.kotlin.ru.datana.smart.ui.ml.models

import com.fasterxml.jackson.databind.ObjectMapper
import kotlin.test.Test
import ru.datana.smart.ui.ml.models.TemperatureUI
import java.time.Instant
import kotlin.test.Ignore
import kotlin.test.assertEquals

class TimeTest {

//    @Ignore
    @Test
    fun timeTest() {
        val json = """
            {"version": "0.1", "boilTime": "2020-09-22T18:26:17.246000Z", "deviceState": "switchedOff"}
        """.trimIndent()

        val objectMapper = ObjectMapper()
        val obj = objectMapper.readValue(json, TemperatureUI::class.java)

        assertEquals("0.1", obj.version)
        assertEquals(Instant.parse("2020-09-22T18:26:17.246Z"), Instant.parse(obj.boilTime))
        assertEquals(TemperatureUI.DeviceState.SWITCHED_OFF, obj.deviceState)
    }
}
