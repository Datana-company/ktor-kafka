package ru.datana.smart.ui.converter.backend

import kotlinx.coroutines.runBlocking
import ru.datana.smart.ui.converter.common.models.*

import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

internal class EventsChainNKR906Test {
    /** Допустимая доля на графике должна меняться в зависимости от значения "METAL_RATE_POINT_WARNING". */
    @Test
    fun isMetalRatePointWarningRightNKR906() {
        runBlocking {
            val timeStart = Instant.now()

            val repository = createRepositoryWithEventForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_WARNING_EVENT,
                timeStart = timeStart.minusMillis(1000L),
                metalRate = 0.11,
                criticalPoint = null,
                warningPoint = 0.1,
                angleStart = 66.0,
                category = ModelEvent.Category.WARNING
            )

            val converterFacade = converterFacadeTest(
                meltTimeout = 5000L,
                roundingWeight = 0.1,
                metalRateWarningPoint = 0.1,
                metalRateCriticalPoint = 0.34,
                reactionTime = 3000,
                currentState = createCurrentStateForTest(
                    lastAngle = 66.0,
                    lastSteelRate = 0.14,
                    avgSteelRate = 0.14
                ),
                converterRepository = repository
            )

            val context = converterBeContextTest(
                timeStart = timeStart,
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    slagRate = 0.001,
                    steelRate = 0.001

                ),
                frame = ModelFrame(
                    frameTime = Instant.now()
                )
            )
            converterFacade.handleMath(context)
            assertEquals(ModelEvent.Category.WARNING, context.events.first().category)
            assertEquals(0.1, context.streamRateWarningPoint)
            assertNotEquals(0.2, context.streamRateWarningPoint)
        }
    }

}
