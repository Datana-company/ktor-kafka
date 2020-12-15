package ru.datana.smart.ui.converter.backend

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import ru.datana.smart.ui.converter.common.models.*

import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

internal class EventsChainNKR1041Test {
    /**NKR-1041
     * ui-converter. По окончанию скачивания шлака последняя рекомендация и световой сигнал не меняют статус (остаются активными)
     */
    @Test
    fun isEventActiveAfterReactionTimeNKR1041() {
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
                    frameTime = timeStart
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
            val timeStart = Instant.now()
            val repository = createRepositoryWithEventForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_WARNING_EVENT,
                timeStart = timeStart.minusMillis(1000L),
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
                    lastSteelRate = 0.14,
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
                    frameTime = timeStart
                )
            )

            converterFacade.handleMath(context)
            assertNotEquals(false, context.events.first().isActive)
            assertNotEquals(SignalerSoundModel.SignalerSoundTypeModel.NONE, context.signaler.sound.type)
        }
    }
}
