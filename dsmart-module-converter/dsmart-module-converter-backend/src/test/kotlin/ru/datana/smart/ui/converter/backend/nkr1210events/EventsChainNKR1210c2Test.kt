package ru.datana.smart.ui.converter.backend.nkr1210events

import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import ru.datana.smart.ui.converter.backend.*
import ru.datana.smart.ui.converter.common.models.*
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class EventsChainNKR1210c2Test {

    /**
     * NKR-1210
     * Проверка, что при достижении среднего значения % металла (шлака) порога streamRateCriticalPoint
     * выдается рекомендация типа "Критическая ситуация"
     */
    @Test
    fun `Show steel critical alert`(){
        runBlocking {
            val timeStart = Instant.now()

            val repository = createRepositoryWithEventForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_INFO_EVENT,
                timeStart = timeStart.minusMillis(5000L),
                metalRate = 0.09,
                warningPoint = 0.1,
                angleStart = 66.0,
                category = ModelEvent.Category.INFO
            )

            val converterFacade = converterFacadeTest(
                roundingWeight = 0.1,
                streamRateWarningPoint = 0.1,
                streamRateCriticalPoint = 0.15,
                reactionTime = 3000L,
                currentState = createCurrentStateForTest(
                    lastAngle = 66.0,
                    avgSteelRate = 0.09
                ),
                converterRepository = repository
            )

            val context = converterBeContextTest(
                timeStart = timeStart,
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    steelRate = 0.74
                ),
                frame = ModelFrame(
                    frameTime = timeStart
                )
            )

            assertEquals(0, context.events.size)

            converterFacade.handleMath(context)
            val event = context.events.first()

            assertEquals(ModelEvent.Category.CRITICAL, event.category)
            assertTrue { event.isActive}
            assertEquals(ModelEvent.ExecutionStatus.NONE, event.executionStatus)
            assertEquals(SignalerModel.SignalerLevelModel.CRITICAL, context.signaler.level )
            assertEquals(SignalerSoundModel.SignalerSoundTypeModel.SOUND_1, context.signaler.sound.type)
        }
    }
}
