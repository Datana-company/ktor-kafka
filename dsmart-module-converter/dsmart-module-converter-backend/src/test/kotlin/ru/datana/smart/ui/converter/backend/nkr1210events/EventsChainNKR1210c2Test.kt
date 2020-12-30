package ru.datana.smart.ui.converter.backend.nkr1210events

import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import ru.datana.smart.ui.converter.backend.*
import ru.datana.smart.ui.converter.common.models.*
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
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

            val currentStateRepository = createCurrentStateRepositoryForTest(
                lastAngle = 66.0,
                avgSteelRate = 0.09
            )

            val converterFacade = converterFacadeTest(
                roundingWeight = 0.1,
                streamRateWarningPoint = 0.1,
                streamRateCriticalPoint = 0.15,
                reactionTime = 3000L,
                currentStateRepository = currentStateRepository
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

            assertEquals(0, context.eventList.size)

            converterFacade.handleMath(context)
            val event = context.eventList.first()

            assertEquals(ModelEvent.Category.CRITICAL, event.category)
            assertTrue { event.isActive}
            assertEquals(ModelEvent.ExecutionStatus.NONE, event.executionStatus)
            assertEquals(ModelSignaler.ModelSignalerLevel.CRITICAL, context.signaler.level )
            assertNotEquals(ModelSignalerSound.ModelSignalerSoundType.NONE, context.signaler.sound.type)
        }
    }

    @Test
    fun `Show slag critical alert`(){
        runBlocking {
            val timeStart = Instant.now()

            val currentStateRepository = createCurrentStateRepositoryForTest(
                lastAngle = 66.0,
                avgSlagRate = 0.09
            )

            val converterFacade = converterFacadeTest(
                roundingWeight = 0.1,
                streamRateWarningPoint = 0.1,
                streamRateCriticalPoint = 0.15,
                reactionTime = 3000L,
                currentStateRepository = currentStateRepository,
                eventMode = ModelEventMode.SLAG
            )

            val context = converterBeContextTest(
                timeStart = timeStart,
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    slagRate = 0.74
                ),
                frame = ModelFrame(
                    frameTime = timeStart
                )
            )

            assertEquals(0, context.eventList.size)

            converterFacade.handleMath(context)
            val event = context.eventList.first()

            assertEquals(ModelEvent.Category.CRITICAL, event.category)
            assertTrue { event.isActive}
            assertEquals(ModelEvent.ExecutionStatus.NONE, event.executionStatus)
            assertEquals(ModelSignaler.ModelSignalerLevel.CRITICAL, context.signaler.level )
            assertNotEquals(ModelSignalerSound.ModelSignalerSoundType.NONE, context.signaler.sound.type)
        }
    }
}
