package ru.datana.smart.ui.converter.backend.nkr1210events

import kotlinx.coroutines.runBlocking
import ru.datana.smart.ui.converter.backend.*
import ru.datana.smart.ui.converter.common.models.*
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

internal class EventsChainNKR1210c16Test {

    /**
     * NKR-1210
     * Проверка статуса критической рекомендации, если % металла  упал ниже streamRateWarningPoint
     * после окончания времени реакции reactionTime
     */
    @Test
    fun `critical even status should be failed and inactive, reactionTime is exceeded`(){
        runBlocking {
            val timeStart = Instant.now()
            val meltTimeout = 10000L

            val repository = createRepositoryWithEventForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_CRITICAL_EVENT,
                timeStart = timeStart.minusMillis(3000L),
                metalRate = 0.16,
                criticalPoint = 0.15,
                warningPoint = 0.1,
                angleStart = 66.0,
                category = ModelEvent.Category.CRITICAL
            )

            val converterFacade = converterFacadeTest(
                meltTimeout = meltTimeout,
                dataTimeout = 3000L,
                roundingWeight = 0.5,
                streamRateWarningPoint = 0.1,
                streamRateCriticalPoint = 0.15,
                reactionTime = 3000L,
                currentState = createCurrentStateForTest(
                    lastAngle = 66.0,
                    avgSteelRate = 0.16
                ),
                converterRepository = repository
            )

            val context = converterBeContextTest(
                timeStart = timeStart,
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    steelRate = 0.03
                ),
                frame = ModelFrame(
                    frameTime = timeStart
                ),
                signalerLevel = SignalerModel.SignalerLevelModel.CRITICAL,
                signalerSoundType = SignalerSoundModel.SignalerSoundTypeModel.SOUND_1
            )

            converterFacade.handleMath(context)

            val oldEvent = context.events.first()

            assertEquals(ModelEvent.Category.CRITICAL, oldEvent.category)
            assertFalse { oldEvent.isActive }
            assertEquals(ModelEvent.ExecutionStatus.FAILED, oldEvent.executionStatus)

            assertEquals(SignalerModel.SignalerLevelModel.NO_SIGNAL, context.signaler.level)
            assertEquals(SignalerSoundModel.SignalerSoundTypeModel.NONE, context.signaler.sound.type)
        }
    }
}
