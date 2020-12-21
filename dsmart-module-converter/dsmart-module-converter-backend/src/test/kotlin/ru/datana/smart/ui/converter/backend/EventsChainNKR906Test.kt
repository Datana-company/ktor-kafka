package ru.datana.smart.ui.converter.backend

import kotlinx.coroutines.runBlocking
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.*

import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

internal class EventsChainNKR906Test {
    /** NKR-906
     * К сожалению  мы пока не имеем возможности в рамках Junit testa в бекенде проверить отрабатывает ли
     * "METAL_RATE_POINT_WARNING" на графике на Фронте.
     *  Можно проверить не теряется ли в контексте переменная, но к задаче снизу тест относится косвенно,
     * так он не покрывает задачу и вообще не ясно ли имеет смысл.
     * Допустимая доля на графике должна меняться в зависимости от значения "METAL_RATE_POINT_WARNING".
     * */
    @Test
    fun isMetalRatePointWarningRightNKR906() {
        runBlocking {
            val timeStart = Instant.now()

            val repository = createRepositoryWithEventForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_WARNING_EVENT,
                timeStart = timeStart.minusMillis(1000L),
                metalRate = 0.11,
                angleStart = 66.0,
                category = ModelEvent.Category.WARNING
            )

            val converterFacade = converterFacadeTest(
                meltTimeout = 5000L,
                roundingWeight = 0.1,
                streamRateWarningPoint = 0.1,
                streamRateCriticalPoint = 0.34,
                reactionTime = 3000,
                currentState = createCurrentStateForTest(
                    lastAngle = 66.0,
                    lastSteelRate = 0.14,
                    avgSteelRate = 0.14
                ),
                converterRepository = repository
            )

            val context = converterBeContextTest(
                timeStart = timeStart,
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    slagRate = 0.001,
                    steelRate = 0.001

                ),
                frame = ModelFrame(
                    frameTime = Instant.now()
                )
            )
            converterFacade.handleMath(context)

            assertEquals(CorStatus.SUCCESS, context.status)
            assertEquals(ModelEvent.Category.WARNING, context.events.first().category)
            assertEquals(0.1, context.streamRateWarningPoint)
            assertNotEquals(0.2, context.streamRateWarningPoint)
        }
    }

}
