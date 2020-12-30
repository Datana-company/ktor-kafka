package ru.datana.smart.ui.converter.backend

import kotlinx.coroutines.runBlocking
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.*
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

internal class SignalerNKR905Test {

    /** NKR-905
     * К сожалению  мы пока не имеем возможности проверить переменные  Environment variables  в рамках Junit testa в бекенде.
     * Можно проверить не теряется ли в контексте переменная, но к задаче снизу тест относится косвенно,
     * так он не покрывает задачу и вообще не ясно ли имеет смысл.
     * [лимит на звуковой сигнал сирены] - в секундах - сколько по длительности должен продолжаться звуковой сигнал
     */
    @Test
    fun isEventActiveAfterSirenLimitTimeNKR905() {
        runBlocking {
            val timeStart = Instant.now()
            val repository = createEventRepositoryForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_CRITICAL_EVENT,
                timeStart = timeStart.minusMillis(3000L),
                angleStart = 66.0,
                category = ModelEvent.Category.CRITICAL
            )

            val stateRepository = createCurrentStateRepositoryForTest(
                lastAngle = 66.0,
                avgSteelRate = 0.16
            )

            val converterFacade = converterFacadeTest(

                roundingWeight = 0.1,
                streamRateWarningPoint = 0.1,
                streamRateCriticalPoint = 0.34,
                reactionTime = 3000,
                sirenLimitTime = 3000,
                currentStateRepository = stateRepository,
                eventRepository = repository
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

            assertEquals(CorStatus.SUCCESS, context.status)
            assertEquals(3000L, context.sirenLimitTime)
            assertNotEquals(1000L, context.sirenLimitTime)
        }
    }
}
