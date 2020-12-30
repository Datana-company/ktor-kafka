package ru.datana.smart.ui.converter.backend

import kotlinx.coroutines.runBlocking
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.*

import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

internal class EventsChainNKR1055Test {
    /** NKR-1055  ModelEvent.ExecutionStatus.FAILED
     *  Предупреждение "Не выполнено" - т.к. истекло время реакции (3 сек), % металла не снизился, угол не уменьшился
     */
    @Test
    fun isExecutionStatusFailedNKR1055() {
        runBlocking {
            val timeStart = Instant.now()

            val repository = createEventRepositoryForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_WARNING_EVENT,
                timeStart = timeStart.minusMillis(5000L),
                angleStart = 60.0,
                category = ModelEvent.Category.WARNING
            )

            val stateRepository = createCurrentStateRepositoryForTest(
                lastAngle = 60.0,
                lastAvgSlagRate = 0.011,
                lastSteelRate = 0.011
            )

            val converterFacade = converterFacadeTest(
                roundingWeight = 0.1,
                streamRateWarningPoint = 0.1,
                streamRateCriticalPoint = 0.34,
                reactionTime = 3000,
                currentStateRepository = stateRepository,
                eventRepository = repository
            )
            val context = converterBeContextTest(
                timeStart = timeStart,
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    steelRate = 0.011
                ),
                frame = ModelFrame(
                    frameTime = timeStart
                ),
            )
            converterFacade.handleMath(context)

            assertEquals(CorStatus.SUCCESS, context.status)
            assertEquals(ModelEvent.Category.WARNING, context.eventList.first().category)
            assertEquals(ModelEvent.ExecutionStatus.FAILED, context.eventList.first().executionStatus)

        }
    }

    /**
     * Не истекло время реакции (3 сек),а угол уменьшился
     * */
    @Test
    fun isExecutionStatusFailedNKR1055_WithFalseParameterTest() {
        runBlocking {
            val timeStart = Instant.now()
            val repository = createEventRepositoryForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_WARNING_EVENT,
                timeStart = timeStart.minusMillis(2000L),
                angleStart = 68.0,
                category = ModelEvent.Category.WARNING
            )

            val stateRepository = createCurrentStateRepositoryForTest(
                lastAngle = 60.0,
                lastSteelRate = 0.011,
                lastAvgSlagRate = 0.11
            )

            val converterFacade = converterFacadeTest(
                roundingWeight = 0.1,
                streamRateWarningPoint = 0.1,
                streamRateCriticalPoint = 0.34,
                reactionTime = 3000,
                currentStateRepository = stateRepository,
                eventRepository = repository
            )
            val context = converterBeContextTest(
                timeStart = timeStart,
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    steelRate = 0.011
                ),
                frame = ModelFrame(
                    frameTime = timeStart
                ),
            )
            converterFacade.handleMath(context)

            assertEquals(CorStatus.SUCCESS, context.status)
            assertEquals(ModelEvent.Category.WARNING, context.eventList.first().category)
            assertNotEquals(ModelEvent.ExecutionStatus.FAILED, context.eventList.first().executionStatus)
            assertNotEquals(ModelEvent.ExecutionStatus.COMPLETED, context.eventList.first().executionStatus)
            assertEquals(ModelEvent.ExecutionStatus.NONE, context.eventList.first().executionStatus)

        }
    }

    /** NKR-1055  ModelEvent.ExecutionStatus.COMPLETED
     *  Предупреждение "Выполнено" - т.к. истекло время реакции (3 сек), % металла снизился,
     *  угол уменьшился не менее, чем на 5 градусов
     */
    @Test
    fun isExecutionStatusComplitedNKR1055() {
        runBlocking {
            val timeStart = Instant.now()
            val repository = createEventRepositoryForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_WARNING_EVENT,
                timeStart = timeStart.minusMillis(5000L),
                angleStart = 68.0,
                category = ModelEvent.Category.WARNING
            )

            val stateRepository = createCurrentStateRepositoryForTest(
                lastAngle = 60.0,
                lastAvgSlagRate = 0.11
            )

            val converterFacade = converterFacadeTest(
                roundingWeight = 0.1,
                streamRateWarningPoint = 0.1,
                streamRateCriticalPoint = 0.34,
                reactionTime = 3000,
                currentStateRepository = stateRepository,
                eventRepository = repository
            )

            val context = converterBeContextTest(
                timeStart = timeStart,
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    steelRate = 0.011
                ),
                frame = ModelFrame(
                    frameTime = timeStart
                ),
            )
            converterFacade.handleMath(context)

            assertEquals(CorStatus.SUCCESS, context.status)
            assertEquals(ModelEvent.Category.WARNING, context.eventList.first().category)
            assertEquals(ModelEvent.ExecutionStatus.COMPLETED, context.eventList.first().executionStatus)

        }
    }

    /** NKR-1055  ModelEvent.ExecutionStatus.FAILED
     *  Предупреждение " не Выполнено" - т.к. истекло время реакции (3 сек), % металла не снизился,
     *  угол уменьшился менее, чем на 5 градусов
     */

    @Test
    fun isExecutionStatusComplitedNKR1055_WithFalseParameterTest() {
        runBlocking {
            val timeStart = Instant.now()

            val repository = createEventRepositoryForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_WARNING_EVENT,
                timeStart = timeStart.minusMillis(5000L),
                angleStart = 68.0,
                category = ModelEvent.Category.WARNING
            )

            val stateRepository = createCurrentStateRepositoryForTest(
                lastAngle = 64.0,
                lastAvgSlagRate = 0.11
            )

            val converterFacade = converterFacadeTest(
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

            assertEquals(CorStatus.SUCCESS, context.status)
            assertEquals(ModelEvent.Category.WARNING, context.eventList.first().category)
            assertNotEquals(ModelEvent.ExecutionStatus.COMPLETED, context.eventList.first().executionStatus)
            assertNotEquals(ModelEvent.ExecutionStatus.FAILED, context.eventList.first().executionStatus)

        }
    }

    /** NKR-1055  ModelEvent.ExecutionStatus.None
     * Предупреждение "без статуса" - т.к.  % металла снизился до допустимой нормы, а время реакции еще не истекло.
     */
    @Test
    fun isExecutionStatusNoneNKR1055() {
        runBlocking {
            val timeStart = Instant.now()

            val repository = createEventRepositoryForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_WARNING_EVENT,
                timeStart = timeStart.minusMillis(2000L),
                angleStart = 68.0,
                category = ModelEvent.Category.WARNING
            )

            val stateRepository = createCurrentStateRepositoryForTest(
                lastAngle = 60.0,
                lastSteelRate = 0.16,
                lastAvgSlagRate = 0.11
            )

            val converterFacade = converterFacadeTest(
                roundingWeight = 0.1,
                streamRateWarningPoint = 0.1,
                streamRateCriticalPoint = 0.34,
                reactionTime = 3000,
                currentStateRepository = stateRepository,
                eventRepository = repository
            )
            val context = converterBeContextTest(
                timeStart = timeStart,
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    steelRate = 0.001
                ),
                frame = ModelFrame(
                    frameTime = timeStart
                ),
            )
            converterFacade.handleMath(context)

            assertEquals(CorStatus.SUCCESS, context.status)
            assertEquals(ModelEvent.Category.WARNING, context.eventList.first().category)
            assertEquals(ModelEvent.ExecutionStatus.NONE, context.eventList.first().executionStatus)

        }
    }

    /** NKR-1055
     * Предупреждение "не Выполнено" - т.к.  % металла не снизился до допустимой нормы, а время реакции истекло.
     */
    @Test
    fun isExecutionStatusNoneNKR1055_WithFalseParameterTest() {
        runBlocking {
            val timeStart = Instant.now()
            val repository = createEventRepositoryForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_WARNING_EVENT,
                timeStart = timeStart.minusMillis(8000L),
                angleStart = 68.0,
                category = ModelEvent.Category.WARNING
            )

            val stateRepository = createCurrentStateRepositoryForTest(
                lastSteelRate = 0.11,
                lastAngle = 68.0,
                lastAvgSlagRate = 0.11
            )

            val converterFacade = converterFacadeTest(
                roundingWeight = 0.1,
                streamRateWarningPoint = 0.1,
                streamRateCriticalPoint = 0.34,
                reactionTime = 3000,
                currentStateRepository = stateRepository,
                eventRepository = repository
            )
            val context = converterBeContextTest(
                timeStart = timeStart,
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    steelRate = 0.11,
                    slagRate = 0.11
                ),
                frame = ModelFrame(
                    frameTime = timeStart
                ),
            )
            converterFacade.handleMath(context)

            assertEquals(CorStatus.SUCCESS, context.status)
            assertEquals(ModelEvent.Category.WARNING, context.eventList.first().category)
            assertNotEquals(ModelEvent.ExecutionStatus.FAILED, context.eventList.first().executionStatus)

        }
    }
}
