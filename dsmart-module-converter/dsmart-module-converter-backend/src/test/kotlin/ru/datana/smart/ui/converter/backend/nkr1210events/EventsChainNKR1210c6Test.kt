package ru.datana.smart.ui.converter.backend.nkr1210events

import kotlinx.coroutines.runBlocking
import ru.datana.smart.ui.converter.backend.*
import ru.datana.smart.ui.converter.common.models.*
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class EventsChainNKR1210c6Test {

    /**
     * NKR-1210
     * Проверка, что статус рекомендации не меняется, если угол наклона уменьшился больше, чем на 5 градусов,
     * но время реакции еще не истекло
     */
    @Test
    fun `event status is still the same after angle decrease but reaction time is no wasted`(){
        runBlocking {
            val timeStart = Instant.now()
            val meltTimeout = 10000L

            val repository = createEventRepositoryForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_WARNING_EVENT,
                timeStart = timeStart.minusMillis(2000L),
                angleStart = 66.0,
                category = ModelEvent.Category.WARNING
            )

            val currentStateRepository = createCurrentStateRepositoryForTest(
                lastAngle = 66.0,
                avgSteelRate = 0.12
            )

            val converterFacade = converterFacadeTest(
                meltTimeout = meltTimeout,
                dataTimeout = 3000L,
                roundingWeight = 0.5,
                streamRateWarningPoint = 0.1,
                streamRateCriticalPoint = 0.15,
                reactionTime = 3000L,
                currentStateRepository = currentStateRepository,
                eventRepository = repository
            )

            val contextAngles = converterBeContextTest(
                timeStart = timeStart,
                meltInfo = defaultMeltInfoTest(),
                angles = ModelAngles(
                    angle = 60.0,
                    angleTime = timeStart
                )
            )

            val contextMath = converterBeContextTest(
                timeStart = timeStart,
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    steelRate = 0.09
                ),
                frame = ModelFrame(
                    frameTime = timeStart
                )
            )

            converterFacade.handleAngles(contextAngles)
            converterFacade.handleMath(contextMath)
            val event = contextMath.eventList.first()

            assertEquals(ModelEvent.Category.WARNING, event.category)
            assertTrue { event.isActive }
            assertEquals(ModelEvent.ExecutionStatus.NONE, event.executionStatus)
        }
    }

    @Test
    fun `slag event status is still the same after angle decrease but reaction time is no wasted`(){
        runBlocking {
            val timeStart = Instant.now()
            val meltTimeout = 10000L

            val repository = createEventRepositoryForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_WARNING_EVENT,
                timeStart = timeStart.minusMillis(2000L),
                angleStart = 66.0,
                category = ModelEvent.Category.WARNING
            )

            val currentStateRepository = createCurrentStateRepositoryForTest(
                lastAngle = 66.0,
                avgSlagRate = 0.12
            )

            val converterFacade = converterFacadeTest(
                meltTimeout = meltTimeout,
                dataTimeout = 3000L,
                roundingWeight = 0.5,
                streamRateWarningPoint = 0.1,
                streamRateCriticalPoint = 0.15,
                reactionTime = 3000L,
                currentStateRepository = currentStateRepository,
                eventRepository = repository,
                eventMode = ModelEventMode.SLAG
            )

            val contextAngles = converterBeContextTest(
                timeStart = timeStart,
                meltInfo = defaultMeltInfoTest(),
                angles = ModelAngles(
                    angle = 60.0,
                    angleTime = timeStart
                )
            )

            val contextMath = converterBeContextTest(
                timeStart = timeStart,
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    slagRate = 0.09
                ),
                frame = ModelFrame(
                    frameTime = timeStart
                )
            )

            converterFacade.handleAngles(contextAngles)
            converterFacade.handleMath(contextMath)
            val event = contextMath.eventList.first()

            assertEquals(ModelEvent.Category.WARNING, event.category)
            assertTrue { event.isActive }
            assertEquals(ModelEvent.ExecutionStatus.NONE, event.executionStatus)
        }
    }
}
