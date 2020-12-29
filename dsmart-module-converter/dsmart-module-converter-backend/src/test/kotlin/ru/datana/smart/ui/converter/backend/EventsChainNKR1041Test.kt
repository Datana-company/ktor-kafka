package ru.datana.smart.ui.converter.backend

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.*

import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

internal class EventsChainNKR1041Test {
    /**
     * NKR-1041
     * По окончанию скачивания шлака (meltTimeout) последняя рекомендация и световой сигнал меняют статус (не остаются активными)
     */
    @Test
    fun isEventActiveAfterReactionTimeNKR1041() {
        runBlocking {
            val timeStart = Instant.now()
            val meltTimeout = 5000L
            val repository = createRepositoryWithEventForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_WARNING_EVENT,
                timeStart = timeStart.minusMillis(1000L),
                angleStart = 66.0,
                category = ModelEvent.Category.WARNING
            )

            val stateRepository = createCurrentStateRepositoryForTest(
                lastAngle = 66.0,
                avgStreamRate = 0.14,
                lastSteelRate = 0.14
            )

            val converterFacade = converterFacadeTest(
                meltTimeout = meltTimeout,
                roundingWeight = 0.1,
                streamRateWarningPoint = 0.1,
                streamRateCriticalPoint = 0.34,
                reactionTime = 3000,
                currentStateRepository = stateRepository,
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
            delay(meltTimeout + 1000L)
            assertEquals(ModelEvent.Category.WARNING, context.eventList.first().category)
            assertEquals(false, context.eventList.first().isActive)
            assertEquals(ModelSignalerSound.NONE, context.signaler.sound)
            assertEquals(ModelSignaler.ModelSignalerLevel.NO_SIGNAL, context.signaler.level)
        }
    }

    // Во время скачивания шлака, последняя рекомендация(Critical) и световой сигнал остаются активными
    @Test
    fun isEventActiveAfterReactionTimeNKR1041_WithFalseParameterTest() {
        runBlocking {
            val timeStart = Instant.now()
            val repository = createRepositoryWithEventForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_WARNING_EVENT,
                timeStart = timeStart.minusMillis(1000L),
                angleStart = 66.0,
                category = ModelEvent.Category.WARNING
            )

            val stateRepository = createCurrentStateRepositoryForTest(
                lastAngle = 66.0,
                avgStreamRate = 0.18,
                lastSteelRate = 0.14
            )

            val converterFacade = converterFacadeTest(
                meltTimeout = 5000L,
                roundingWeight = 0.1,
                streamRateWarningPoint = 0.1,
                streamRateCriticalPoint = 0.13,
                reactionTime = 3000,
                currentStateRepository = stateRepository,
                converterRepository = repository
            )

            val context = converterBeContextTest(
                timeStart = timeStart,
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

            assertEquals(CorStatus.SUCCESS, context.status)
            assertNotEquals(false, context.eventList.first().isActive)
            assertNotEquals(ModelSignalerSound.ModelSignalerSoundType.NONE, context.signaler.sound.type)
            assertNotEquals(ModelSignaler.ModelSignalerLevel.NO_SIGNAL, context.signaler.level)
            assertEquals(ModelSignalerSound.ModelSignalerSoundType.SOUND_1, context.signaler.sound.type)
            assertEquals(ModelSignaler.ModelSignalerLevel.CRITICAL, context.signaler.level)
        }
    }
}
