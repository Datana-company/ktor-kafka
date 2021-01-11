package ru.datana.smart.ui.converter.backend.nkr1210events

import kotlinx.coroutines.runBlocking
import ru.datana.smart.ui.converter.backend.*
import ru.datana.smart.ui.converter.common.models.*
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class EventsChainNKR1210c10Test {

    /**
     * NKR-1210
     * Проверка, что при снижении  среднего значения % металла (шлака) ниже streamRateCriticalPoint, но все еще выше
     * streamRateWarningPoint, текущая рекомендация становится неактивной и появляется рекомендация "Предупреждение".
     * Статус рекомендации "Критическая" не выставляется, т.к. не истекло время реакции (reactionTime).
     */
    @Test
    fun `warn event after metal decrease below crit level, crit event status same, reactionTime no exceed`(){
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

            val context1 = converterBeContextTest(
                timeStart = timeStart,
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    steelRate = 0.16
                ),
                frame = ModelFrame(
                    frameTime = timeStart
                )
            )

            val context2 = converterBeContextTest(
                timeStart = timeStart,
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    steelRate = 0.12
                ),
                frame = ModelFrame(
                    frameTime = timeStart
                )
            )

            converterFacade.handleMath(context1)
            var oldEvent = context1.eventList.first()

            assertEquals(ModelEvent.Category.CRITICAL, oldEvent.category)
            assertEquals(ModelEvent.ExecutionStatus.NONE, oldEvent.executionStatus)
            assertTrue { oldEvent.isActive }
            assertEquals(ModelSignaler.ModelSignalerLevel.CRITICAL, context1.signaler.level)
            assertEquals(ModelSignalerSound.ModelSignalerSoundType.SOUND_1, context1.signaler.sound.type)


            converterFacade.handleMath(context2)
            oldEvent = context2.eventList.last()
            val newEvent = context2.eventList.first()

            assertEquals(ModelEvent.Category.CRITICAL, oldEvent.category)
            assertEquals(ModelEvent.ExecutionStatus.NONE, oldEvent.executionStatus)
            assertFalse { oldEvent.isActive }
            assertEquals(ModelSignaler.ModelSignalerLevel.WARNING, context2.signaler.level)
            assertEquals(ModelSignalerSound.ModelSignalerSoundType.NONE, context2.signaler.sound.type)
            assertEquals(ModelEvent.Category.WARNING, newEvent.category)
            assertEquals(ModelEvent.ExecutionStatus.NONE, newEvent.executionStatus)
            assertTrue { newEvent.isActive }

        }
    }

    @Test
    fun `warn event after slag decrease below crit level, crit event status same, reactionTime no exceed`(){
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

            val context1 = converterBeContextTest(
                timeStart = timeStart,
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    slagRate = 0.16
                ),
                frame = ModelFrame(
                    frameTime = timeStart
                )
            )

            val context2 = converterBeContextTest(
                timeStart = timeStart,
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    slagRate = 0.12
                ),
                frame = ModelFrame(
                    frameTime = timeStart
                )
            )

            converterFacade.handleMath(context1)
            var oldEvent = context1.eventList.first()

            assertEquals(ModelEvent.Category.CRITICAL, oldEvent.category)
            assertEquals(ModelEvent.ExecutionStatus.NONE, oldEvent.executionStatus)
            assertTrue { oldEvent.isActive }
            assertEquals(ModelSignaler.ModelSignalerLevel.CRITICAL, context1.signaler.level)
            assertEquals(ModelSignalerSound.ModelSignalerSoundType.SOUND_1, context1.signaler.sound.type)


            converterFacade.handleMath(context2)
            oldEvent = context2.eventList.last()
            val newEvent = context2.eventList.first()

            assertEquals(ModelEvent.Category.CRITICAL, oldEvent.category)
            assertEquals(ModelEvent.ExecutionStatus.NONE, oldEvent.executionStatus)
            assertFalse { oldEvent.isActive }
            assertEquals(ModelSignaler.ModelSignalerLevel.WARNING, context2.signaler.level)
            assertEquals(ModelSignalerSound.ModelSignalerSoundType.NONE, context2.signaler.sound.type)
            assertEquals(ModelEvent.Category.WARNING, newEvent.category)
            assertEquals(ModelEvent.ExecutionStatus.NONE, newEvent.executionStatus)
            assertTrue { newEvent.isActive }

        }
    }
}
