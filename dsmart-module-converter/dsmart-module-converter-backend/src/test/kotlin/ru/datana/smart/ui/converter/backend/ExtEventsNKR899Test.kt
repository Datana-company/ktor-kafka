package ru.datana.smart.ui.converter.backend

import kotlinx.coroutines.runBlocking
import ru.datana.smart.ui.converter.common.models.ModelEvent
import kotlin.test.Test
import kotlin.test.assertEquals

internal class ExtEventsNKR899Test {

    @Test
    fun testExtEvent() {
        runBlocking {
            val converterFacade = converterFacadeTest(
                currentState = createCurrentStateForTest()
            )

            val context = converterBeContextTest(
                meltInfo = defaultMeltInfoTest(),
                extEvent = ModelEvent(
                    alertRuleId = "alertRuleId_234",
                    component = "component_123",
                    timestamp = "timestamp"
                )
            )

            converterFacade.handleExtEvents(context)
            assertEquals("alertRuleId_234", context.events.first().alertRuleId)
            assertEquals("component_123", context.events.first().component)
            assertEquals("timestamp", context.events.first().timestamp)
        }
    }
}
