package ru.datana.smart.ui.converter.backend

import kotlinx.coroutines.runBlocking
import ru.datana.smart.ui.converter.common.models.*
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

internal class SignalerNKR905Test {

    /**NKR-905
     * [лимит на звуковой сигнал сирены] - в секундах - сколько по длительности должен продолжаться звуковой сигнал
     * [время реакции на рекомендацию] - REACTION_TIME   - Уже проверено в EventsChainTest
     * [допустимый % потери] - METAL_RATE_POINT_WARNING - Уже проверено в EventsChainTest
     * [% критической потери металла] - METAL_RATE_POINT_CRITICAL - Уже проверено в EventsChainTest
     */
    @Test
    fun isEventActiveAfterSirenLimitTimeNKR905() {
        runBlocking {
            val timeStart = Instant.now()

            val repository = createRepositoryWithEventForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_CRITICAL_EVENT,
                timeStart = timeStart.minusMillis(3000L),
                metalRate = 0.16,
                criticalPoint = 0.15,
                angleStart = 66.0,
                category = ModelEvent.Category.CRITICAL
            )

            val converterFacade = converterFacadeTest(

                roundingWeight = 0.1,
                streamRateWarningPoint = 0.1,
                streamRateCriticalPoint = 0.34,
                reactionTime = 3000,
                sirenLimitTime = 3000,
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
                    slagRate = 0.001,
                    steelRate = 0.16

                ),
                frame = ModelFrame(
                    frameTime = timeStart
                )
            )

            converterFacade.handleMath(context)
            assertEquals(3000L, context.sirenLimitTime)
            assertNotEquals(1000L, context.sirenLimitTime)
        }
    }
}
