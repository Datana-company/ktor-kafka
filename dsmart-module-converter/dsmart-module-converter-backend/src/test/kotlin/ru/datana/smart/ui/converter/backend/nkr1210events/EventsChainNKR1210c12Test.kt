package ru.datana.smart.ui.converter.backend.nkr1210events

import kotlinx.coroutines.runBlocking
import ru.datana.smart.ui.converter.backend.*
import ru.datana.smart.ui.converter.common.models.*
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class EventsChainNKR1210c12Test {

    /**
     * NKR-1210
     * Проверка, что при снижении  среднего значения % металла (шлака) ниже streamRateCriticalPoint, но все еще выше
     * streamRateWarningPoint, текущая рекомендация становится неактивной и появляется рекомендация "Предупреждение".
     * Статус рекомендации "Критическая" выставляется в "выполнено", т.к. истекло время реакции (reactionTime)
     * и изменился улог наклона конвертера больше, чем на 5 градусов.
     */
    @Test
    fun `warn event after metal decrease below crit level, crit event status completed, angle is decreased`(){
        runBlocking {
            val timeStart = Instant.now()
            val meltTimeout = 10000L

            val repository = createEventRepositoryForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_CRITICAL_EVENT,
                timeStart = timeStart.minusMillis(3000L),
                angleStart = 66.0,
                category = ModelEvent.Category.CRITICAL
            )

            val currentStateRepository = createCurrentStateRepositoryForTest(
                lastAngle = 60.0,
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
                    steelRate = 0.12
                ),
                frame = ModelFrame(
                    frameTime = timeStart
                ),
                signalerLevel = ModelSignaler.ModelSignalerLevel.CRITICAL,
                signalerSoundType = ModelSignalerSound.ModelSignalerSoundType.SOUND_1
            )

            converterFacade.handleMath(context)
            val newEvent = context.eventList.first()
            val oldEvent = context.eventList.last()

            assertEquals(ModelEvent.Category.CRITICAL, oldEvent.category)
            assertFalse { oldEvent.isActive }
            assertEquals(ModelEvent.ExecutionStatus.COMPLETED, oldEvent.executionStatus)
            assertEquals(ModelEvent.Category.WARNING, newEvent.category)
            assertTrue { newEvent.isActive }
            assertEquals(ModelEvent.ExecutionStatus.NONE, newEvent.executionStatus)

            assertEquals(ModelSignaler.ModelSignalerLevel.WARNING, context.signaler.level)
            assertEquals(ModelSignalerSound.ModelSignalerSoundType.NONE, context.signaler.sound.type)
        }
    }

    @Test
    fun `warn event after slag decrease below crit level, crit event status completed, angle is decreased`(){
        runBlocking {
            val timeStart = Instant.now()
            val meltTimeout = 10000L

            val repository = createEventRepositoryForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_CRITICAL_EVENT,
                timeStart = timeStart.minusMillis(3000L),
                angleStart = 66.0,
                category = ModelEvent.Category.CRITICAL
            )

            val currentStateRepository = createCurrentStateRepositoryForTest(
                lastAngle = 60.0,
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
                    slagRate = 0.12
                ),
                frame = ModelFrame(
                    frameTime = timeStart
                ),
                signalerLevel = ModelSignaler.ModelSignalerLevel.CRITICAL,
                signalerSoundType = ModelSignalerSound.ModelSignalerSoundType.SOUND_1
            )

            converterFacade.handleMath(context)
            val newEvent = context.eventList.first()
            val oldEvent = context.eventList.last()

            assertEquals(ModelEvent.Category.CRITICAL, oldEvent.category)
            assertFalse { oldEvent.isActive }
            assertEquals(ModelEvent.ExecutionStatus.COMPLETED, oldEvent.executionStatus)
            assertEquals(ModelEvent.Category.WARNING, newEvent.category)
            assertTrue { newEvent.isActive }
            assertEquals(ModelEvent.ExecutionStatus.NONE, newEvent.executionStatus)

            assertEquals(ModelSignaler.ModelSignalerLevel.WARNING, context.signaler.level)
            assertEquals(ModelSignalerSound.ModelSignalerSoundType.NONE, context.signaler.sound.type)
        }
    }
}
