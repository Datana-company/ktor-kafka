package ru.datana.smart.ui.converter.backend

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.*

import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

internal class EventsChainNKR1080Test {
    /** NKR-1080
     *  последняя рекомендация не должна быть отмечена статусом "Выполнено", т. к угол наклона не изменился,
     *  рекомендация выдалалась и плавка закончилась, последняя рекомендация должна просто уйти в историю без статуса
     *  currentState.get().currentMeltInfo.id.isEmpty()
     */
    @Test
    fun isExecutionStatusNoneIfMeltFinishNKR1080() {
        runBlocking {
            val timeStart = Instant.now()
            val repository = createEventRepositoryForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_WARNING_EVENT,
                timeStart = timeStart.minusMillis(1000L),
                angleStart = 60.0,
                category = ModelEvent.Category.WARNING
            )

            val stateRepository = createCurrentStateRepositoryForTest(
                lastAngle = 60.0,
                lastSteelRate = 0.011,
                lastAvgSlagRate = 0.11
            )

            val converterFacade = converterFacadeTest(
                meltTimeout = 3000L,
                roundingWeight = 0.1,
                streamRateWarningPoint = 0.1,
                streamRateCriticalPoint = 0.16,
                reactionTime = 3000L,
                currentStateRepository = stateRepository,
                eventRepository = repository
            )

            val context = converterBeContextTest(
                timeStart = timeStart,
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    steelRate = 0.12
                ),
                frame = ModelFrame(
                    frameTime = timeStart
                ),
            )
            converterFacade.handleMath(context)
            delay(6000)

            assertEquals(ModelEvent.Category.WARNING, context.eventList.first().category)
            assertEquals(ModelEvent.ExecutionStatus.NONE, context.eventList.first().executionStatus)
            assertEquals(false, context.eventList.first().isActive)
            assertEquals("", context.meltInfo.id)
        }
    }

    @Test
    fun isExecutionStatusNoneIfMeltFinishNKR1080_WithFalseParameterTest() {
        runBlocking {
            val timeStart = Instant.now()
            val repository = createEventRepositoryForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_WARNING_EVENT,
                timeStart =timeStart.minusMillis(1000L),
                angleStart = 68.0,
                category = ModelEvent.Category.WARNING
            )

            val stateRepository = createCurrentStateRepositoryForTest(
                lastAngle = 60.0,
                lastSteelRate = 0.011,
                lastAvgSlagRate = 0.011
            )

            val converterFacade = converterFacadeTest(
                meltTimeout = 3000L,
                roundingWeight = 0.1,
                streamRateWarningPoint = 0.1,
                streamRateCriticalPoint = 0.16,
                reactionTime = 1000L,
                currentStateRepository = stateRepository,
                eventRepository = repository
            )

            val context = converterBeContextTest(
                timeStart = timeStart,
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    steelRate = 0.011,
                    slagRate = 0.00
                ),
                frame = ModelFrame(
                    frameTime = timeStart
                ),
            )
            converterFacade.handleMath(context)

            assertEquals(CorStatus.SUCCESS, context.status)
            assertNotEquals(ModelEvent.ExecutionStatus.NONE, context.eventList.first().executionStatus)
            assertNotEquals(true, context.eventList.first().isActive)
            assertNotEquals("", context.meltInfo.id)
        }
    }
}
