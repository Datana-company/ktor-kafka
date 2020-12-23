package ru.datana.smart.ui.converter.backend.nkr1210events

import kotlinx.coroutines.runBlocking
import ru.datana.smart.ui.converter.backend.*
import ru.datana.smart.ui.converter.common.models.*
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class EventsChainNKR1210c1Test {

    /**
     * NKR-1210
     * Проверка, что при достижении среднего значения % металла (шлака) порога streamRateWarningPoint
     * выдается рекомендация типа "Предупреждение"
     */
    @Test
    fun `Show steel warning alert`(){
        runBlocking {
            val timeStart = Instant.now()

            val converterFacade = converterFacadeTest(
                roundingWeight = 0.1,
                streamRateWarningPoint = 0.1,
                streamRateCriticalPoint = 0.15,
                reactionTime = 3000L,
                currentState = createCurrentStateForTest(
                    lastAngle = 66.0,
                    avgSteelRate = 0.09
                )
            )

            val context = converterBeContextTest(
                timeStart = timeStart,
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    steelRate = 0.25
                ),
                frame = ModelFrame(
                    frameTime = timeStart
                )
            )

            converterFacade.handleMath(context)
            val  event = context.events.first()

            assertEquals(ModelEvent.Category.WARNING, event.category)
            assertTrue { event.isActive}
            assertEquals(SignalerModel.SignalerLevelModel.WARNING, context.signaler.level)
            assertEquals(SignalerSoundModel.SignalerSoundTypeModel.NONE, context.signaler.sound.type)
        }
    }
}
