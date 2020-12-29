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
                currentStateRepository = createCurrentStateRepositoryForTest()
            )

            val context = converterBeContextTest(
                meltInfo = defaultMeltInfoTest(),
                externalEvent = ModelEvent(
                    alertRuleId = "alertRuleId_234",
                    component = "component_123",
                    timestamp = "timestamp"
                )
            )

            converterFacade.handleExternalEvents(context)
            assertEquals("alertRuleId_234", context.eventList.first().alertRuleId)
            assertEquals("component_123", context.eventList.first().component)
            assertEquals("timestamp", context.eventList.first().timestamp)
        }
    }
}
