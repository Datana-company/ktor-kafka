package ru.datana.smart.ui.converter.backend.nkr1210events

import kotlinx.coroutines.runBlocking
import ru.datana.smart.ui.converter.backend.*
import ru.datana.smart.ui.converter.common.models.*
import java.time.Instant
import kotlin.test.*

internal class EventsChainNKR1210c18Test {

    /**
     * NKR-1210
     * Проверка, что сирена отключается при достижении предела ее звучания sirenLimitTime
     * FAILED
     */
    //@Test
    fun `siren is off after sirenLimitTime`(){
        runBlocking {
            val timeStart = Instant.now()
            val sirenTimeOut = 5000L

            val repository = createRepositoryWithEventForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_CRITICAL_EVENT,
                timeStart = timeStart.minusMillis(3000L),
                angleStart = 66.0,
                category = ModelEvent.Category.CRITICAL
            )

            val converterFacade = converterFacadeTest(
                sirenLimitTime = sirenTimeOut,
                roundingWeight = 0.5,
                streamRateWarningPoint = 0.1,
                streamRateCriticalPoint = 0.15,
                reactionTime = 3000L,
                currentState = createCurrentStateForTest(
                    lastAngle = 66.0,
                    avgStreamRate = 0.16
                ),
                converterRepository = repository
            )

            val context1 = converterBeContextTest(
                timeStart = timeStart,
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    steelRate = 0.18
                ),
                frame = ModelFrame(
                    frameTime = timeStart
                ),
                signalerLevel = ModelSignaler.ModelSignalerLevel.CRITICAL,
                signalerSoundType = ModelSignalerSound.ModelSignalerSoundType.SOUND_1,
                sirenLimitTime = sirenTimeOut.toInt()
            )

            val context2 = converterBeContextTest(
                timeStart = timeStart.plusMillis(2000L),
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    steelRate = 0.18
                ),
                frame = ModelFrame(
                    frameTime = timeStart.plusMillis(2000L)
                )
            )

            converterFacade.handleMath(context1)
            var newEvent = context1.events.first()
            var oldEvent = context1.events.last()

            assertEquals(ModelEvent.Category.CRITICAL, oldEvent.category)
            assertEquals(ModelEvent.ExecutionStatus.FAILED, oldEvent.executionStatus)
            assertFalse { oldEvent.isActive }
            assertEquals(ModelEvent.Category.CRITICAL, newEvent.category)
            assertEquals(ModelEvent.ExecutionStatus.NONE, newEvent.executionStatus)
            assertTrue { newEvent.isActive }
            assertEquals(ModelSignaler.ModelSignalerLevel.CRITICAL, context1.signaler.level)
            assertNotEquals(ModelSignalerSound.ModelSignalerSoundType.NONE, context1.signaler.sound.type)


            converterFacade.handleMath(context2)
            oldEvent = context2.events.last()
            newEvent = context2.events.first()

            assertEquals(2, context2.events.size)
            assertEquals(ModelEvent.Category.CRITICAL, oldEvent.category)
            assertEquals(ModelEvent.ExecutionStatus.FAILED, oldEvent.executionStatus)
            assertFalse { oldEvent.isActive }
            assertEquals(ModelSignaler.ModelSignalerLevel.CRITICAL, context2.signaler.level)
            assertEquals(ModelSignalerSound.ModelSignalerSoundType.NONE, context2.signaler.sound.type)
            assertEquals(ModelEvent.Category.CRITICAL, newEvent.category)
            assertEquals(ModelEvent.ExecutionStatus.NONE, newEvent.executionStatus)
            assertTrue { newEvent.isActive }

        }
    }
}
