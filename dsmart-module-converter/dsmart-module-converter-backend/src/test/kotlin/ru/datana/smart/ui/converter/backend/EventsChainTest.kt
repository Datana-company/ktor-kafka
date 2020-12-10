package ru.datana.smart.ui.converter.backend


import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import ru.datana.smart.ui.converter.common.models.*

import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals

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
                    lastAngleTime = null,
                    lastAngle = 66.0,
                    lastSource = null,
                    lastSteelRate = 0.16,
                    lastSlagRate = null
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
        }
    }

    /** NKR-1055  ModelEvent.ExecutionStatus.FAILED
     *  Предупреждение "Не выполнено" - т.к. истекло время реакции (3 сек), % металла не снизился, угол не уменьшился
     */
    @Test
    fun isExecutionStatusFAILEDNKR1055() {
        runBlocking {
            val repository = createRepositoryWithEventForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_WARNING_EVENT,
                timeStart = Instant.now().minusMillis(5000L),
                metalRate = 0.11,
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
                    lastAngleTime = null,
                    lastAngle = 60.0,
                    lastSource = null,
                    lastSteelRate = 0.11,
                    lastSlagRate = null
                ),
                converterRepository = repository
            )
            val context = converterBeContextTest(
//            reactionTime = 3000L,
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
                    lastAngleTime = null,
                    lastAngle = 60.0,
                    lastSource = null,
                    lastSteelRate = 0.11,
                    lastSlagRate = null
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
                eventType= ModelEvent.EventType.STREAM_RATE_WARNING_EVENT,
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
                    lastAngleTime = null,
                    lastAngle = 60.0,
                    lastSource = null,
                    lastSteelRate = 0.16,
                    lastSlagRate = null),
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
    /** NKR-1080  ModelEvent.ExecutionStatus.StatusNone
     *  последняя рекомендация не должна быть отмечена статусом "Выполнено", т. к угол наклона не изменился,
     *  рекомендация выдалалась и плавка закончилась, последняя рекомендация должна просто уйти в историю без статуса
     *  currentState.get().currentMeltInfo.id.isEmpty()
     */
    @Test
    fun isExecutionStatusNoneIfMeltFinishNKR1080() {
        runBlocking {
            val repository =  createRepositoryWithEventForTest(
               eventType =  ModelEvent.EventType.STREAM_RATE_WARNING_EVENT,
                timeStart = Instant.now().minusMillis(1000L),
                metalRate = 0.011,
                warningPoint = 0.1,
                angleStart = 60.0,
                category = ModelEvent.Category.WARNING
            )

            val converterFacade = converterFacadeTest(
                meltTimeout=5000L,
                roundingWeight = 0.1,
                metalRateWarningPoint = 0.1,
                metalRateCriticalPoint = 0.16,
                reactionTime = 3000L,
                currentState = createCurrentStateForTest(
                    lastAngle = 60.0,
                    lastSteelRate = 0.011),
                converterRepository = repository
            )

            val context = converterBeContextTest(
//                reactionTime = 3000L,
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    steelRate = 0.011
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
//            assertEquals(ModelEvent.ExecutionStatus.COMPLETED, context.events.first().executionStatus)

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
                timeStart = Instant.now().minusMillis(11000L),
                metalRate = 0.11,
                criticalPoint = null,
                warningPoint = 0.1,
                angleStart = 66.0,
                category = ModelEvent.Category.WARNING
            )

            val converterFacade = converterFacadeTest(
                roundingWeight = 0.1,
                metalRateWarningPoint = 0.1,
                metalRateCriticalPoint = 0.34,
                reactionTime = 3000,
                currentState = createCurrentStateForTest(null, 66.0, null, 0.14, null),
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
            assertEquals(false, context.events.first().isActive)
            assertEquals(SignalerSoundModel.NONE, context.signaler.sound)
        }
    }
}
