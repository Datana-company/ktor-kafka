package ru.datana.smart.ui.converter.angle.app

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Test
import ru.datana.smart.ui.converter.angle.app.models.AngleSchedule
import java.io.File
import kotlin.test.assertEquals

class AngleTest {

    @Test
    fun deserializeTest(): Unit {
        val json = File("resources/selsyn.json").readText(Charsets.UTF_8)
        val schedule = ObjectMapper().configure(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS, true).readValue(
            json,
            AngleSchedule::class.java
        )
        assertEquals(12, schedule.items?.size)
        print(schedule)
    }
}
