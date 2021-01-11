package ru.datana.smart.ui.converter.backend.nkr1210events

import kotlinx.coroutines.runBlocking
import ru.datana.smart.ui.converter.backend.*
import ru.datana.smart.ui.converter.common.models.*
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

internal class EventsChainNKR1210c17Test {

    /**
     * NKR-1210
     * Проверка статуса критической рекомендации, если % металла  упал ниже streamRateWarningPoint
     * после окончания времени реакции reactionTime и уменьшении  угола наклона больше, чем на 5*
     */
    @Test
    fun `critical event is completed if angle decrease, steelRate decrease and reactionTime is exceeded`(){
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

            val contextAngles = converterBeContextTest(
                timeStart = timeStart,
                meltInfo = defaultMeltInfoTest(),
                angles = ModelAngles(
                    angle = 60.0,
                    angleTime = timeStart
                )
            )

            val contextMath = converterBeContextTest(
                timeStart = timeStart,
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    steelRate = 0.03
                ),
                frame = ModelFrame(
                    frameTime = timeStart
                )
            )

            converterFacade.handleAngles(contextAngles)
            converterFacade.handleMath(contextMath)
            val oldEvent = contextMath.eventList.last()

            assertEquals(ModelEvent.Category.CRITICAL, oldEvent.category)
            assertFalse { oldEvent.isActive }
            assertEquals(ModelEvent.ExecutionStatus.COMPLETED, oldEvent.executionStatus)
            assertEquals(ModelSignaler.ModelSignalerLevel.NO_SIGNAL, contextMath.signaler.level)
            assertEquals(ModelSignalerSound.ModelSignalerSoundType.NONE, contextMath.signaler.sound.type)
        }
    }

    @Test
    fun `critical event is completed if angle decrease, slagRate decrease and reactionTime is exceeded`(){
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

            val contextAngles = converterBeContextTest(
                timeStart = timeStart,
                meltInfo = defaultMeltInfoTest(),
                angles = ModelAngles(
                    angle = 60.0,
                    angleTime = timeStart
                )
            )

            val contextMath = converterBeContextTest(
                timeStart = timeStart,
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    slagRate = 0.03
                ),
                frame = ModelFrame(
                    frameTime = timeStart
                )
            )

            converterFacade.handleAngles(contextAngles)
            converterFacade.handleMath(contextMath)
            val oldEvent = contextMath.eventList.last()

            assertEquals(ModelEvent.Category.CRITICAL, oldEvent.category)
            assertFalse { oldEvent.isActive }
            assertEquals(ModelEvent.ExecutionStatus.COMPLETED, oldEvent.executionStatus)
            assertEquals(ModelSignaler.ModelSignalerLevel.NO_SIGNAL, contextMath.signaler.level)
            assertEquals(ModelSignalerSound.ModelSignalerSoundType.NONE, contextMath.signaler.sound.type)
        }
    }
}
