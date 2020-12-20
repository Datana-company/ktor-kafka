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

            val repository = createRepositoryWithEventForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_WARNING_EVENT,
                timeStart = timeStart.minusMillis(5000L),
                metalRate = 0.11,
                angleStart = 60.0,
                category = ModelEvent.Category.WARNING
            )

            val converterFacade = converterFacadeTest(
                roundingWeight = 0.1,
                streamRateWarningPoint = 0.1,
                streamRateCriticalPoint = 0.34,
                reactionTime = 3000,
                currentState = createCurrentStateForTest(
                    lastAngle = 60.0,
                    lastSteelRate = 0.011,
                    avgSteelRate = 0.011
                ),
                converterRepository = repository
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
            assertEquals(ModelEvent.Category.WARNING, context.events.first().category)
            assertEquals(ModelEvent.ExecutionStatus.FAILED, context.events.first().executionStatus)

        }
    }

    /**
     * Не истекло время реакции (3 сек),а угол уменьшился
     * */
    @Test
    fun isExecutionStatusFailedNKR1055_WithFalseParameterTest() {
        runBlocking {
            val timeStart = Instant.now()
            val repository = createRepositoryWithEventForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_WARNING_EVENT,
                timeStart = timeStart.minusMillis(2000L),
                metalRate = 0.011,
                angleStart = 68.0,
                category = ModelEvent.Category.WARNING
            )

            val converterFacade = converterFacadeTest(
                roundingWeight = 0.1,
                streamRateWarningPoint = 0.1,
                streamRateCriticalPoint = 0.34,
                reactionTime = 3000,
                currentState = createCurrentStateForTest(
                    lastAngle = 60.0,
                    lastSteelRate = 0.011,
                    avgSteelRate = 0.11
                ),
                converterRepository = repository
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
            assertEquals(ModelEvent.Category.WARNING, context.events.first().category)
            assertNotEquals(ModelEvent.ExecutionStatus.FAILED, context.events.first().executionStatus)
            assertNotEquals(ModelEvent.ExecutionStatus.COMPLETED, context.events.first().executionStatus)
            assertEquals(ModelEvent.ExecutionStatus.STATELESS, context.events.first().executionStatus)

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
            val repository = createRepositoryWithEventForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_WARNING_EVENT,
                timeStart = timeStart.minusMillis(5000L),
                metalRate = 0.11,
                angleStart = 68.0,
                category = ModelEvent.Category.WARNING
            )

            val converterFacade = converterFacadeTest(
                roundingWeight = 0.1,
                streamRateWarningPoint = 0.1,
                streamRateCriticalPoint = 0.34,
                reactionTime = 3000,
                currentState = createCurrentStateForTest(
                    lastAngle = 60.0,
                    avgSteelRate = 0.11
                ),
                converterRepository = repository
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
            assertEquals(ModelEvent.Category.WARNING, context.events.first().category)
            assertEquals(ModelEvent.ExecutionStatus.COMPLETED, context.events.first().executionStatus)

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

            val repository = createRepositoryWithEventForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_WARNING_EVENT,
                timeStart = timeStart.minusMillis(5000L),
                metalRate = 0.11,
                angleStart = 68.0,
                category = ModelEvent.Category.WARNING
            )

            val converterFacade = converterFacadeTest(
                roundingWeight = 0.1,
                streamRateWarningPoint = 0.1,
                streamRateCriticalPoint = 0.16,
                reactionTime = 3000L,
                currentState = createCurrentStateForTest(
                    lastAngle = 64.0,
                    avgSteelRate = 0.11
                ),
                converterRepository = repository
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
            assertEquals(ModelEvent.Category.WARNING, context.events.first().category)
            assertNotEquals(ModelEvent.ExecutionStatus.COMPLETED, context.events.first().executionStatus)
            assertNotEquals(ModelEvent.ExecutionStatus.FAILED, context.events.first().executionStatus)

        }
    }

    /** NKR-1055  ModelEvent.ExecutionStatus.None
     * Предупреждение "без статуса" - т.к.  % металла снизился до допустимой нормы, а время реакции еще не истекло.
     */
    @Test
    fun isExecutionStatusNoneNKR1055() {
        runBlocking {
            val timeStart = Instant.now()

            val repository = createRepositoryWithEventForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_WARNING_EVENT,
                timeStart = timeStart.minusMillis(2000L),
                metalRate = 0.16,
                angleStart = 68.0,
                category = ModelEvent.Category.WARNING
            )
            val converterFacade = converterFacadeTest(
                roundingWeight = 0.1,
                streamRateWarningPoint = 0.1,
                streamRateCriticalPoint = 0.34,
                reactionTime = 3000,
                currentState = createCurrentStateForTest(
                    lastAngle = 60.0,
                    lastSteelRate = 0.16,
                    avgSteelRate = 0.11
                ),
                converterRepository = repository
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
            assertEquals(ModelEvent.Category.WARNING, context.events.first().category)
            assertEquals(ModelEvent.ExecutionStatus.STATELESS, context.events.first().executionStatus)

        }
    }

    /** NKR-1055
     * Предупреждение "не Выполнено" - т.к.  % металла не снизился до допустимой нормы, а время реакции истекло.
     */
    @Test
    fun isExecutionStatusNoneNKR1055_WithFalseParameterTest() {
        runBlocking {
            val timeStart = Instant.now()
            val repository = createRepositoryWithEventForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_WARNING_EVENT,
                timeStart = timeStart.minusMillis(8000L),
                angleStart = 68.0,
                category = ModelEvent.Category.WARNING
            )
            val converterFacade = converterFacadeTest(
                roundingWeight = 0.1,
                streamRateWarningPoint = 0.1,
                streamRateCriticalPoint = 0.34,
                reactionTime = 3000,
                currentState = createCurrentStateForTest(
                    lastSteelRate = 0.11,
                    lastAngle = 68.0,
                    avgSteelRate = 0.11
                ),
                converterRepository = repository
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
            assertEquals(ModelEvent.Category.WARNING, context.events.first().category)
            assertNotEquals(ModelEvent.ExecutionStatus.FAILED, context.events.first().executionStatus)

        }
    }
}
