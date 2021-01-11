package ru.datana.smart.ui.converter.backend.nkr1210events

import kotlinx.coroutines.runBlocking
import ru.datana.smart.ui.converter.backend.*
import ru.datana.smart.ui.converter.common.models.*
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

internal class EventsChainNKR1210c5Test {

    /**
     * NKR-1210
     * Проверка, что, если по истечении времени реакции ReactionTime угол наклона уменьшился больше, чем на 5 градусов,
     * и снизился % металла, то рекомендация помечается как выполненная, а лампочка гаснет.
     */
    @Test
    fun `event is done after angle and steelRate decrease`(){
        runBlocking {
            val timeStart = Instant.now()
            val meltTimeout = 10000L

            val repository = createEventRepositoryForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_WARNING_EVENT,
                timeStart = timeStart.minusMillis(3000L),
                angleStart = 66.0,
                category = ModelEvent.Category.WARNING
            )

            val currentStateRepository = createCurrentStateRepositoryForTest(
                avgSteelRate = 0.12,
                lastAngle = 66.0
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
                    steelRate = 0.06
                ),
                frame = ModelFrame(
                    frameTime = timeStart
                )
            )

            converterFacade.handleAngles(contextAngles)
            converterFacade.handleMath(contextMath)

            val event = contextMath.eventList.first()

            assertEquals(ModelEvent.Category.WARNING, event.category)
            assertFalse { event.isActive }
            assertEquals(ModelEvent.ExecutionStatus.COMPLETED, event.executionStatus)
            assertEquals(ModelSignaler.ModelSignalerLevel.NO_SIGNAL, contextMath.signaler.level)
            assertEquals(ModelSignalerSound.ModelSignalerSoundType.NONE, contextMath.signaler.sound.type)


        }
    }

    @Test
    fun `event is done after angle and slagRate decrease`(){
        runBlocking {
            val timeStart = Instant.now()
            val meltTimeout = 10000L

            val repository = createEventRepositoryForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_WARNING_EVENT,
                timeStart = timeStart.minusMillis(3000L),
                angleStart = 66.0,
                category = ModelEvent.Category.WARNING
            )

            val currentStateRepository = createCurrentStateRepositoryForTest(
                avgSlagRate = 0.12,
                lastAngle = 66.0
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
                    slagRate = 0.06
                ),
                frame = ModelFrame(
                    frameTime = timeStart
                )
            )

            converterFacade.handleAngles(contextAngles)
            converterFacade.handleMath(contextMath)
            val event = contextMath.eventList.first()

            assertEquals(ModelEvent.Category.WARNING, event.category)
            assertFalse { event.isActive }
            assertEquals(ModelEvent.ExecutionStatus.COMPLETED, event.executionStatus)
            assertEquals(ModelSignaler.ModelSignalerLevel.NO_SIGNAL, contextMath.signaler.level)
            assertEquals(ModelSignalerSound.ModelSignalerSoundType.NONE, contextMath.signaler.sound.type)
        }
    }
}
