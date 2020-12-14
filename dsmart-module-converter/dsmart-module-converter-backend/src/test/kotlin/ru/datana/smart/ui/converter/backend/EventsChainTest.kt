package ru.datana.smart.ui.converter.backend


import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import ru.datana.smart.ui.converter.common.models.*

import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

internal class EventsChainTest {
    /**  NKR-1031
     *  Если есть активная рекомендация об изменении угла конвертера и при этом потери «металла»
     *  сами по себе вернулись в пределы допустимой нормы, такая рекомендация должна становиться бледной,
     *  независимо от того появилась ли сверху новая рекомендация или нет.
     */
    @Test
    fun isEventActiveNKR1031() {
        runBlocking {
            val repository = createRepositoryWithEventForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_CRITICAL_EVENT,
                timeStart = Instant.now().minusMillis(1000L),
                metalRate = 0.16,
                criticalPoint = 0.15,
                warningPoint = 0.1,
                angleStart = 66.0,
                category = ModelEvent.Category.CRITICAL
            )

            val converterFacade = converterFacadeTest(
                roundingWeight = 0.5,
                currentState = createCurrentStateForTest(
                    lastAngle = 60.0,
                    avgSteelRate = 0.16
                ),
                converterRepository = repository
            )

            val context = converterBeContextTest(
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

            assertEquals(ModelEvent.Category.CRITICAL, context.events.first().category)
            assertEquals(false, context.events.first().isActive)
            assertNotEquals(ModelEvent.Category.WARNING, context.events.first().category)
            assertNotEquals(true, context.events.first().isActive)
        }
    }
    @Test
    fun isEventActiveNKR1031_WithFalseParameterTest() {
        runBlocking {
            val repository = createRepositoryWithEventForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_CRITICAL_EVENT,
                timeStart = Instant.now().minusMillis(1000L),
                metalRate = 0.16,
                criticalPoint = 0.15,
                warningPoint = 0.1,
                angleStart = 66.0,
                category = ModelEvent.Category.CRITICAL
            )

            val converterFacade = converterFacadeTest(
                roundingWeight = 0.5,
                currentState = createCurrentStateForTest(
                    lastAngle = 66.0,
                    avgSteelRate = 0.16
                ),
                converterRepository = repository
            )

            val context = converterBeContextTest(
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    slagRate = 0.16,
                    steelRate = 0.16
                ),
                frame = ModelFrame(
                    frameTime = Instant.now()
                )
            )
            converterFacade.handleMath(context)

            assertEquals(true, context.events.first().isActive)
            assertNotEquals(false, context.events.first().isActive)
        }
    }
    /** NKR-1055  ModelEvent.ExecutionStatus.FAILED
     *  Предупреждение "Не выполнено" - т.к. истекло время реакции (3 сек), % металла не снизился, угол не уменьшился
     */
    @Test
    fun isExecutionStatusFailedNKR1055() {
        runBlocking {
            val repository = createRepositoryWithEventForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_WARNING_EVENT,
                timeStart = Instant.now().minusMillis(7000L),
                metalRate = 0.011,
                criticalPoint = null,
                warningPoint = 0.1,
                angleStart = 60.0,
                category = ModelEvent.Category.WARNING
            )

            val converterFacade = converterFacadeTest(
                roundingWeight = 0.1,
                metalRateWarningPoint = 0.1,
                metalRateCriticalPoint = 0.34,
                reactionTime = 3000,
                currentState = createCurrentStateForTest(
                    lastAngle = 60.0,
                    lastSteelRate = 0.011,
                    avgSteelRate = 0.011
                ),
                converterRepository = repository
            )
            val context = converterBeContextTest(
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    steelRate = 0.011
                ),
                frame = ModelFrame(
                    frameTime = Instant.now()
                ),
            )
            converterFacade.handleMath(context)

            assertEquals(ModelEvent.Category.WARNING, context.events.first().category)
            assertEquals(ModelEvent.ExecutionStatus.FAILED, context.events.first().executionStatus)

        }
    }
//Не истекло время реакции (3 сек),а угол уменьшился
    @Test
    fun isExecutionStatusFailedNKR1055_WithFalseParameterTest() {
    runBlocking {
        val repository = createRepositoryWithEventForTest(
            eventType = ModelEvent.EventType.STREAM_RATE_WARNING_EVENT,
            timeStart = Instant.now().minusMillis(3000L),
            metalRate = 0.011,
            criticalPoint = null,
            warningPoint = 0.1,
            angleStart = 68.0,
            category = ModelEvent.Category.WARNING
        )

        val converterFacade = converterFacadeTest(
            roundingWeight = 0.1,
            metalRateWarningPoint = 0.1,
            metalRateCriticalPoint = 0.34,
            reactionTime = 3000,
            currentState = createCurrentStateForTest(
                lastAngle = 60.0,
                lastSteelRate = 0.011,
                avgSteelRate = 0.011
            ),
            converterRepository = repository
        )
        val context = converterBeContextTest(
            meltInfo = defaultMeltInfoTest(),
            slagRate = ModelSlagRate(
                steelRate = 0.011
            ),
            frame = ModelFrame(
                frameTime = Instant.now()
            ),
        )
        converterFacade.handleMath(context)

        assertEquals(ModelEvent.Category.WARNING, context.events.first().category)
        assertNotEquals(ModelEvent.ExecutionStatus.FAILED, context.events.first().executionStatus)
        assertEquals(ModelEvent.ExecutionStatus.COMPLETED, context.events.first().executionStatus)

    }
    }

    /** NKR-1055  ModelEvent.ExecutionStatus.COMPLETED
     *  Предупреждение "Выполнено" - т.к. истекло время реакции (3 сек), % металла не снизился,
     *  угол уменьшился не менее, чем на 5 градусов
     */
    @Test
    fun isExecutionStatusCOMPLETEDNKR1055() {
        runBlocking {
            val repository = createRepositoryWithEventForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_WARNING_EVENT,
                timeStart = Instant.now().minusMillis(5000L),
                metalRate = 0.11,
                criticalPoint = null,
                warningPoint = 0.1,
                angleStart = 68.0,
                category = ModelEvent.Category.WARNING
            )

            val converterFacade = converterFacadeTest(
                roundingWeight = 0.1,
                metalRateWarningPoint = 0.1,
                metalRateCriticalPoint = 0.34,
                reactionTime = 3000,
                currentState = createCurrentStateForTest(
                    lastAngle = 60.0,
                    avgSteelRate = 0.11
                ),
                converterRepository = repository
            )

            val context = converterBeContextTest(
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    steelRate = 0.011
                ),
                frame = ModelFrame(
                    frameTime = Instant.now()
                ),
            )
            converterFacade.handleMath(context)

            assertEquals(ModelEvent.Category.WARNING, context.events.first().category)
            delay(4000)
            assertEquals(ModelEvent.ExecutionStatus.COMPLETED, context.events.first().executionStatus)

        }
    }

//    /** NKR-1055  ModelEvent.ExecutionStatus.COMPLETED
//         *  Предупреждение "Выполнено" - т.к. истекло время реакции (3 сек), % металла не снизился,
//         *  угол уменьшился не менее, чем на 5 градусов
//         */
    @Test
    fun isExecutionStatusCOMPLETEDNKR1055_WithFalseParameterTest() {
        runBlocking {
            val repository = createRepositoryWithEventForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_WARNING_EVENT,
                timeStart = Instant.now().minusMillis(5000L),
                metalRate = 0.11,
                criticalPoint = null,
                warningPoint = 0.1,
                angleStart = 68.0,
                category = ModelEvent.Category.WARNING
            )

            val converterFacade = converterFacadeTest(
                roundingWeight = 0.1,
                metalRateWarningPoint = 0.1,
                metalRateCriticalPoint = 0.34,
                reactionTime = 3000,
                currentState = createCurrentStateForTest(
                    lastAngle = 60.0,
                    avgSteelRate = 0.11
                ),
                converterRepository = repository
            )

            val context = converterBeContextTest(
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    steelRate = 0.011
                ),
                frame = ModelFrame(
                    frameTime = Instant.now()
                ),
            )
            converterFacade.handleMath(context)

            assertEquals(ModelEvent.Category.WARNING, context.events.first().category)
            delay(4000)
            assertEquals(ModelEvent.ExecutionStatus.COMPLETED, context.events.first().executionStatus)

        }
    }

    /** NKR-1055  ModelEvent.ExecutionStatus.None
     * Предупреждение "без статуса" - т.к.  % металла снизился до допустимой нормы, а время реакции еще не истекло.
     */
    @Test
    fun isExecutionStatusNoneNKR1055() {
        runBlocking {
            val repository = createRepositoryWithEventForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_WARNING_EVENT,
                timeStart = Instant.now().minusMillis(2000L),
                metalRate = 0.16,
                criticalPoint = null,
                warningPoint = 0.1,
                angleStart = 68.0,
                category = ModelEvent.Category.WARNING
            )
            val converterFacade = converterFacadeTest(
                roundingWeight = 0.1,
                metalRateWarningPoint = 0.1,
                metalRateCriticalPoint = 0.34,
                reactionTime = 3000,
                currentState = createCurrentStateForTest(
                    lastAngle = 60.0,
                    lastSteelRate = 0.16,
                    avgSteelRate = 0.11
                ),
                converterRepository = repository
            )
            val context = converterBeContextTest(
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    steelRate = 0.001
                ),
                frame = ModelFrame(
                    frameTime = Instant.now()
                ),
            )
            converterFacade.handleMath(context)

            assertEquals(ModelEvent.Category.WARNING, context.events.first().category)
            assertEquals(ModelEvent.ExecutionStatus.NONE, context.events.first().executionStatus)

        }
    }

    /** NKR-1055  ModelEvent.ExecutionStatus.None
     * Предупреждение "без статуса" - т.к.  % металла не снизился до допустимой нормы, а время реакции еще не истекло.
     */
    @Test
    fun isExecutionStatusNoneNKR1055_WithFalseParameterTest() {
        runBlocking {
            val repository = createRepositoryWithEventForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_WARNING_EVENT,
                timeStart = Instant.now().minusMillis(8000L),
                metalRate = 0.01,
                criticalPoint = null,
                warningPoint = 0.1,
                angleStart = 68.0,
                category = ModelEvent.Category.WARNING
            )
            val converterFacade = converterFacadeTest(
                roundingWeight = 0.1,
                metalRateWarningPoint = 0.1,
                metalRateCriticalPoint = 0.16,
                reactionTime = 3000,
                currentState = createCurrentStateForTest(
                    lastSteelRate = 0.01,
                    lastAngle = 68.0,
                    avgSteelRate = 0.01
                ),
                converterRepository = repository
            )
            val context = converterBeContextTest(
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    steelRate = 0.11,
                    slagRate = 0.11

                ),
                frame = ModelFrame(
                    frameTime = Instant.now()
                ),
            )
            converterFacade.handleMath(context)

            assertEquals(ModelEvent.Category.WARNING, context.events.first().category)
            assertNotEquals(ModelEvent.ExecutionStatus.NONE, context.events.first().executionStatus)

        }
    }


    /** NKR-1080  ModelEvent.ExecutionStatus.StatusNone
     *  последняя рекомендация не должна быть отмечена статусом "Выполнено", т. к угол наклона не изменился,
     *  рекомендация выдалалась и плавка закончилась, последняя рекомендация должна просто уйти в историю без статуса
     */
    @Test
    fun isExecutionStatusNoneIfMeltFinishNKR1080() {
        runBlocking {
            val repository = createRepositoryWithEventForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_WARNING_EVENT,
                timeStart = Instant.now().minusMillis(1000L),
                metalRate = 0.011,
                warningPoint = 0.1,
                angleStart = 60.0,
                category = ModelEvent.Category.WARNING
            )

            val converterFacade = converterFacadeTest(
                meltTimeout = 3000L,
                roundingWeight = 0.1,
                metalRateWarningPoint = 0.1,
                metalRateCriticalPoint = 0.16,
                reactionTime = 3000L,
                currentState = createCurrentStateForTest(
                    lastAngle = 60.0,
                    lastSteelRate = 0.011,
                    avgSteelRate = 0.11
                ),
                converterRepository = repository
            )

            val context = converterBeContextTest(
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    steelRate = 0.12
                ),
                frame = ModelFrame(
                    frameTime = Instant.now()
                ),
            )
            converterFacade.handleMath(context)
            delay(6000)

            assertEquals(ModelEvent.Category.WARNING, context.events.first().category)
            assertEquals(ModelEvent.ExecutionStatus.NONE, context.events.first().executionStatus)
            assertEquals(false, context.events.first().isActive)
            assertEquals("",context.currentState.get().currentMeltInfo.id)
        }
    }

    @Test
    fun isExecutionStatusNoneIfMeltFinishNKR1080_WithFalseParameterTest() {
        runBlocking {
            val repository = createRepositoryWithEventForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_WARNING_EVENT,
                timeStart = Instant.now().minusMillis(1000L),
                metalRate = 0.011,
                warningPoint = 0.1,
                angleStart = 68.0,
                category = ModelEvent.Category.WARNING
            )

            val converterFacade = converterFacadeTest(
                meltTimeout = 3000L,
                roundingWeight = 0.1,
                metalRateWarningPoint = 0.1,
                metalRateCriticalPoint = 0.16,
                reactionTime = 1000L,
                currentState = createCurrentStateForTest(
                    lastAngle = 60.0,
                    lastSteelRate = 0.011,
                    avgSteelRate = 0.011
                ),
                converterRepository = repository
            )

            val context = converterBeContextTest(
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    steelRate = 0.011,
                    slagRate = 0.00
                ),
                frame = ModelFrame(
                    frameTime = Instant.now()
                ),
            )
            converterFacade.handleMath(context)
            assertNotEquals(ModelEvent.ExecutionStatus.NONE, context.events.first().executionStatus)
            assertNotEquals(true, context.events.first().isActive)
            assertNotEquals("",context.currentState.get().currentMeltInfo.id)
        }
    }

    /**NKR-1041
     * ui-converter. По окончанию скачивания шлака последняя рекомендация и световой сигнал не меняют статус (остаются активными)
     */
    @Test
    fun isEventActiveAfterReactionTimeNKR1041() {
        runBlocking {
            val repository = createRepositoryWithEventForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_WARNING_EVENT,
                timeStart = Instant.now().minusMillis(1000L),
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
                    lastSteelRate =  0.14,
                    avgSteelRate = 0.14
                ),
                converterRepository = repository
            )

            val context = converterBeContextTest(
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
            delay(6000)
            assertEquals(ModelEvent.Category.WARNING, context.events.first().category)
            assertEquals(false, context.events.first().isActive)
            assertEquals(SignalerSoundModel.NONE, context.signaler.sound)
        }
    }

    @Test
    fun isEventActiveAfterReactionTimeNKR1041_WithFalseParameterTest() {
        runBlocking {
            val repository = createRepositoryWithEventForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_WARNING_EVENT,
                timeStart = Instant.now().minusMillis(1000L),
                metalRate = 0.001,
                criticalPoint = null,
                warningPoint = 0.1,
                angleStart = 66.0,
                category = ModelEvent.Category.WARNING
            )

            val converterFacade = converterFacadeTest(
                meltTimeout = 5000L,
                roundingWeight = 0.1,
                metalRateWarningPoint = 0.1,
                metalRateCriticalPoint = 0.13,
                reactionTime = 3000,
                currentState = createCurrentStateForTest(
                    lastAngle = 66.0,
                    lastSteelRate =  0.14,
                    avgSteelRate = 0.18
                ),
                converterRepository = repository
            )

            val context = converterBeContextTest(
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    slagRate = 0.018,
                    steelRate = 0.18

                ),
                frame = ModelFrame(
                    frameTime = Instant.now()
                )
            )

            converterFacade.handleMath(context)
            assertNotEquals(false, context.events.first().isActive)
            assertNotEquals(SignalerSoundModel.SignalerSoundTypeModel.NONE, context.signaler.sound.type)
        }
    }
    /** Допустимая доля на графике должна меняться в зависимости от значения "METAL_RATE_POINT_WARNING". */
    @Test
    fun isMetalRatePointWarningRightNKR906() {
        runBlocking {
            val repository = createRepositoryWithEventForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_WARNING_EVENT,
                timeStart = Instant.now().minusMillis(1000L),
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
                    lastSteelRate =  0.14,
                    avgSteelRate = 0.14),
                converterRepository = repository
            )

            val context = converterBeContextTest(
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
            assertEquals(0.1, context.events.first().warningPoint)
            assertNotEquals(0.2, context.events.first().warningPoint)
        }
    }

}
