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
                lastAngle = 60.0,
                avgSteelRate = 0.12
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
                    steelRate = 0.06
                ),
                frame = ModelFrame(
                    frameTime = timeStart
                ),
                signalerLevel = ModelSignaler.ModelSignalerLevel.WARNING
            )

            converterFacade.handleMath(context)
            val event = context.eventList.first()

            assertEquals(ModelEvent.Category.WARNING, event.category)
            assertFalse { event.isActive }
            assertEquals(ModelEvent.ExecutionStatus.COMPLETED, event.executionStatus)
            assertEquals(ModelSignaler.ModelSignalerLevel.NO_SIGNAL, context.signaler.level)
            assertEquals(ModelSignalerSound.ModelSignalerSoundType.NONE, context.signaler.sound.type)
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
                lastAngle = 60.0,
                avgSlagRate = 0.12
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
                    slagRate = 0.06
                ),
                frame = ModelFrame(
                    frameTime = timeStart
                ),
                signalerLevel = ModelSignaler.ModelSignalerLevel.WARNING
            )

            converterFacade.handleMath(context)
            val event = context.eventList.first()

            assertEquals(ModelEvent.Category.WARNING, event.category)
            assertFalse { event.isActive }
            assertEquals(ModelEvent.ExecutionStatus.COMPLETED, event.executionStatus)
            assertEquals(ModelSignaler.ModelSignalerLevel.NO_SIGNAL, context.signaler.level)
            assertEquals(ModelSignalerSound.ModelSignalerSoundType.NONE, context.signaler.sound.type)
        }
    }
}
