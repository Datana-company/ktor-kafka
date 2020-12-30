package ru.datana.smart.ui.converter.backend.nkr1210events

import kotlinx.coroutines.runBlocking
import ru.datana.smart.ui.converter.backend.*
import ru.datana.smart.ui.converter.common.models.*
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class EventsChainNKR1210c15Test {

    /**
     * NKR-1210
     * Проверка статуса критической рекомендации, если % металла  упал ниже streamRateWarningPoint
     * до окончания времени реакции reactionTime
     */
    @Test
    fun `critical even status should be none and inactive, reactionTime is not exceeded`(){
        runBlocking {
            val timeStart = Instant.now()
            val meltTimeout = 10000L

            val repository = createEventRepositoryForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_CRITICAL_EVENT,
                timeStart = timeStart.minusMillis(1000L),
                angleStart = 66.0,
                category = ModelEvent.Category.CRITICAL
            )

            val currentStateRepository = createCurrentStateRepositoryForTest(
                lastAngle = 66.0,
                avgSteelRate = 0.16
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

            val context = converterBeContextTest(
                timeStart = timeStart,
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    steelRate = 0.03
                ),
                frame = ModelFrame(
                    frameTime = timeStart
                ),
                signalerLevel = ModelSignaler.ModelSignalerLevel.CRITICAL,
                signalerSoundType = ModelSignalerSound.ModelSignalerSoundType.SOUND_1
            )

            converterFacade.handleMath(context)

            val oldEvent = context.eventList.first()

            assertEquals(ModelEvent.Category.CRITICAL, oldEvent.category)
            assertFalse { oldEvent.isActive }
            assertEquals(ModelEvent.ExecutionStatus.NONE, oldEvent.executionStatus)

            assertEquals(ModelSignaler.ModelSignalerLevel.NO_SIGNAL, context.signaler.level)
            assertEquals(ModelSignalerSound.ModelSignalerSoundType.NONE, context.signaler.sound.type)
        }
    }

    @Test
    fun `slag critical even status should be none and inactive, reactionTime is not exceeded`(){
        runBlocking {
            val timeStart = Instant.now()
            val meltTimeout = 10000L

            val repository = createEventRepositoryForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_CRITICAL_EVENT,
                timeStart = timeStart.minusMillis(1000L),
                angleStart = 66.0,
                category = ModelEvent.Category.CRITICAL
            )

            val currentStateRepository = createCurrentStateRepositoryForTest(
                lastAngle = 66.0,
                avgSlagRate = 0.16
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

            val context = converterBeContextTest(
                timeStart = timeStart,
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    slagRate = 0.03
                ),
                frame = ModelFrame(
                    frameTime = timeStart
                ),
                signalerLevel = ModelSignaler.ModelSignalerLevel.CRITICAL,
                signalerSoundType = ModelSignalerSound.ModelSignalerSoundType.SOUND_1
            )

            converterFacade.handleMath(context)

            val oldEvent = context.eventList.first()

            assertEquals(ModelEvent.Category.CRITICAL, oldEvent.category)
            assertFalse { oldEvent.isActive }
            assertEquals(ModelEvent.ExecutionStatus.NONE, oldEvent.executionStatus)

            assertEquals(ModelSignaler.ModelSignalerLevel.NO_SIGNAL, context.signaler.level)
            assertEquals(ModelSignalerSound.ModelSignalerSoundType.NONE, context.signaler.sound.type)
        }
    }
}
