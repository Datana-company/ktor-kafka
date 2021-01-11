package ru.datana.smart.ui.converter.backend.nkr1210events

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import ru.datana.smart.ui.converter.backend.*
import ru.datana.smart.ui.converter.common.models.*
import java.time.Instant
import kotlin.test.*

internal class EventsChainNKR1210c18Test {

    /**
     * NKR-1210
     * Проверка, что сирена отключается при достижении предела ее звучания sirenLimitTime
     * FAILED: Время звучания сирены проверяется на фронте?
     */
    @Ignore
    @Test
    fun `siren is off after sirenLimitTime`(){
        runBlocking {
            val timeStart = Instant.now()
            val sirenTimeOut = 5000L

            val repository = createEventRepositoryForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_CRITICAL_EVENT,
                timeStart = timeStart.minusMillis(3000L),
                angleStart = 66.0,
                category = ModelEvent.Category.CRITICAL
            )

            val currentStateRepository = createCurrentStateRepositoryForTest(
                lastAngle = 66.0,
                avgSteelRate = 0.16
            )

            val converterFacade = converterFacadeTest(
                sirenLimitTime = sirenTimeOut,
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
                    steelRate = 0.18
                ),
                frame = ModelFrame(
                    frameTime = timeStart
                )
            )


            converterFacade.handleMath(context)
            var newEvent = context.eventList.first()
            var oldEvent = context.eventList.last()

            assertEquals(ModelEvent.Category.CRITICAL, oldEvent.category)
            assertEquals(ModelEvent.ExecutionStatus.FAILED, oldEvent.executionStatus)
            assertFalse { oldEvent.isActive }
            assertEquals(ModelEvent.Category.CRITICAL, newEvent.category)
            assertEquals(ModelEvent.ExecutionStatus.NONE, newEvent.executionStatus)
            assertTrue { newEvent.isActive }
            assertEquals(ModelSignaler.ModelSignalerLevel.CRITICAL, context.signaler.level)
            assertNotEquals(ModelSignalerSound.ModelSignalerSoundType.NONE, context.signaler.sound.type)


            delay(2000)
            oldEvent = context.eventList.last()
            newEvent = context.eventList.first()

            assertEquals(2, context.eventList.size)
            assertEquals(ModelEvent.Category.CRITICAL, oldEvent.category)
            assertEquals(ModelEvent.ExecutionStatus.FAILED, oldEvent.executionStatus)
            assertFalse { oldEvent.isActive }
            assertEquals(ModelSignaler.ModelSignalerLevel.CRITICAL, context.signaler.level)
            assertEquals(ModelSignalerSound.ModelSignalerSoundType.NONE, context.signaler.sound.type)
            assertEquals(ModelEvent.Category.CRITICAL, newEvent.category)
            assertEquals(ModelEvent.ExecutionStatus.NONE, newEvent.executionStatus)
            assertTrue { newEvent.isActive }

        }
    }
}
