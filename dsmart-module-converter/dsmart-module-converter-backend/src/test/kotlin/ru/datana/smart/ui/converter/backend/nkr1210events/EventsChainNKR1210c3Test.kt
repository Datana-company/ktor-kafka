package ru.datana.smart.ui.converter.backend.nkr1210events

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import ru.datana.smart.ui.converter.backend.*
import ru.datana.smart.ui.converter.common.models.*
import ru.datana.smart.ui.converter.common.utils.toPercent
import java.time.Instant
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class EventsChainNKR1210c3Test {

    /**
     * NKR-1210
     * Проверка, что повляется новая рекомендация типа "Предупреждение", если % металла не упал
     * ниже streamRateWarningPoint  и истекло время реакции reactionTime
     */
    @Test
    fun `Show steel warning alert after reaction time`(){
        runBlocking {
            val timeStart = Instant.now()
            val meltTimeout = 10000L

            val repository = createRepositoryWithEventForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_WARNING_EVENT,
                timeStart = timeStart.minusMillis(2000L),
                angleStart = 66.0,
                category = ModelEvent.Category.WARNING
            )

            val converterFacade = converterFacadeTest(
                meltTimeout = meltTimeout,
                dataTimeout = 3000L,
                roundingWeight = 0.1,
                streamRateWarningPoint = 0.1,
                streamRateCriticalPoint = 0.15,
                reactionTime = 3000L,
                currentState = createCurrentStateForTest(
                    lastAngle = 66.0,
                    avgStreamRate = 0.12
                ),
                converterRepository = repository
            )

            val context1 = converterBeContextTest(
                timeStart = timeStart,
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    steelRate = 0.13
                ),
                frame = ModelFrame(
                    frameTime = timeStart
                ),
                signalerLevel = ModelSignaler.ModelSignalerLevel.WARNING
            )

            val context2 = converterBeContextTest(
                timeStart = timeStart.plusMillis(1000L),
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    steelRate = 0.13
                ),
                frame = ModelFrame(
                    frameTime = timeStart.plusMillis(1000L)
                )
            )

            converterFacade.handleMath(context1)
            var oldEvent = context1.events.first()

            assertEquals(ModelEvent.Category.WARNING, oldEvent.category)
            assertEquals(ModelEvent.ExecutionStatus.NONE, oldEvent.executionStatus)
            assertTrue { oldEvent.isActive }
            assertEquals(ModelSignaler.ModelSignalerLevel.WARNING, context1.signaler.level)
            assertEquals(ModelSignalerSound.ModelSignalerSoundType.NONE, context1.signaler.sound.type)


            converterFacade.handleMath(context2)
            oldEvent = context2.events.last()
            val newEvent = context2.events.first()

            assertEquals(ModelEvent.Category.WARNING, oldEvent.category)
            assertEquals(ModelEvent.ExecutionStatus.FAILED, oldEvent.executionStatus)
            assertFalse { oldEvent.isActive }
            assertEquals(ModelSignaler.ModelSignalerLevel.WARNING, context2.signaler.level)
            assertEquals(ModelSignalerSound.ModelSignalerSoundType.NONE, context2.signaler.sound.type)
            assertEquals(ModelEvent.Category.WARNING, newEvent.category)
            assertEquals(ModelEvent.ExecutionStatus.NONE, newEvent.executionStatus)
            assertTrue { newEvent.isActive }

        }
    }
}
