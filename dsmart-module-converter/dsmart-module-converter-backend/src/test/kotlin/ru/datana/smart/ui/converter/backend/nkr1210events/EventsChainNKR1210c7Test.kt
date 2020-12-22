package ru.datana.smart.ui.converter.backend.nkr1210events

import kotlinx.coroutines.runBlocking
import ru.datana.smart.ui.converter.backend.*
import ru.datana.smart.ui.converter.common.models.*
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class EventsChainNKR1210c7Test {

    /**
     * NKR-1210
     * Проверка, что, если по истечении времени реакции ReactionTime угол наклона уменьшился больше, чем на 5 градусов,
     * но % металла не снизился, то рекомендация помечается как выполненная, создается новая рекомендация
     * "Предупреждение",  лампочка остается желтого цвета.
     */
    @Test
    fun `event is completed then angle decrease but steelRate is the same, new event raised`(){
        runBlocking {
            val timeStart = Instant.now()
            val meltTimeout = 10000L

            val repository = createRepositoryWithEventForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_WARNING_EVENT,
                timeStart = timeStart.minusMillis(3000L),
                metalRate = 0.12,
                warningPoint = 0.1,
                angleStart = 66.0,
                category = ModelEvent.Category.WARNING
            )

            val converterFacade = converterFacadeTest(
                meltTimeout = meltTimeout,
                dataTimeout = 3000L,
                roundingWeight = 0.5,
                streamRateWarningPoint = 0.1,
                streamRateCriticalPoint = 0.15,
                reactionTime = 3000L,
                currentState = createCurrentStateForTest(
                    lastAngle = 60.0,
                    avgSteelRate = 0.12
                ),
                converterRepository = repository
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
                signalerLevel = SignalerModel.SignalerLevelModel.WARNING,
                signalerSoundType = SignalerSoundModel.SignalerSoundTypeModel.NONE
            )

            converterFacade.handleMath(context)
            val newEvent = context.events.first()
            val oldEvent = context.events.last()

            assertEquals(ModelEvent.Category.WARNING, oldEvent.category)
            assertFalse { oldEvent.isActive }
            assertEquals(ModelEvent.ExecutionStatus.COMPLETED, oldEvent.executionStatus)
            assertEquals(ModelEvent.Category.WARNING, newEvent.category)
            assertTrue { newEvent.isActive }
            assertEquals(ModelEvent.ExecutionStatus.NONE, newEvent.executionStatus)

            assertEquals(SignalerModel.SignalerLevelModel.WARNING, context.signaler.level)
            assertEquals(SignalerSoundModel.SignalerSoundTypeModel.NONE, context.signaler.sound.type)
        }
    }

}
