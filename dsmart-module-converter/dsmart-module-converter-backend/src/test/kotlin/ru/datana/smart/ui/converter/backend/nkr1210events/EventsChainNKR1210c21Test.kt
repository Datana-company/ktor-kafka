package ru.datana.smart.ui.converter.backend.nkr1210events

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import ru.datana.smart.ui.converter.backend.converterBeContextTest
import ru.datana.smart.ui.converter.backend.converterFacadeTest
import ru.datana.smart.ui.converter.backend.createCurrentStateRepositoryForTest
import ru.datana.smart.ui.converter.backend.defaultMeltInfoTest
import ru.datana.smart.ui.converter.common.models.*
import java.time.Instant
import kotlin.test.*

internal class EventsChainNKR1210c21Test {
    /**
     * NKR-1210
     * Проверка изменения статуса рекомендации критическая ситуация и обнуления данных
     * в зависимости от значений dataTimeout и meltTimeout
     * FAILED: пока не реализована задача NKR-1222
     */
    @Ignore
    @Test
    fun `steel critical should be inactive, data should be reset after dataTimeout`(){
        runBlocking {
            val timeStart = Instant.now()
            val dataTimeout = 5000L

            val currentStateRepository = createCurrentStateRepositoryForTest(
                lastAngle = 66.0,
                avgSteelRate = 0.16
            )

            val converterFacade = converterFacadeTest(
                dataTimeout = dataTimeout,
                roundingWeight = 0.5,
                streamRateWarningPoint = 0.1,
                streamRateCriticalPoint = 0.15,
                reactionTime = 3000L,
                currentStateRepository = currentStateRepository
            )

            val context = converterBeContextTest(
                timeStart = timeStart,
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    steelRate = 0.16
                ),
                angles = ModelAngles(
                    angle = 66.0
                ),
                frame = ModelFrame(
                    frameTime = timeStart
                )
            )

            converterFacade.handleMath(context)

            delay(dataTimeout - 100L)

            assertEquals(1, context.eventList.size)
            var event = context.eventList.first()
            assertEquals(ModelEvent.Category.WARNING, event.category)
            assertEquals(ModelEvent.ExecutionStatus.NONE, event.executionStatus)
            assertTrue { event.isActive }
            assertEquals(ModelSignaler.ModelSignalerLevel.WARNING, context.signaler.level)
            assertEquals(ModelSignalerSound.ModelSignalerSoundType.NONE, context.signaler.sound.type)

            delay(1000L)

            assertEquals(1, context.eventList.size)
            event = context.eventList.first()
            assertEquals(ModelEvent.Category.WARNING, event.category)
            assertEquals(ModelEvent.ExecutionStatus.NONE, event.executionStatus)
            assertFalse { event.isActive }
            assertEquals(ModelAngles.NONE, context.angles)
            assertEquals(ModelSlagRate.NONE, context.slagRate)
            assertEquals(ModelSignaler.ModelSignalerLevel.NO_SIGNAL, context.signaler.level)
            assertEquals(ModelSignalerSound.ModelSignalerSoundType.NONE, context.signaler.sound.type)

        }
    }

    @Ignore
    @Test
    fun `slag critical should be inactive, data should be reset after dataTimeout`(){
        runBlocking {
            val timeStart = Instant.now()
            val dataTimeout = 5000L

            val currentStateRepository = createCurrentStateRepositoryForTest(
                lastAngle = 66.0,
                avgSlagRate = 0.16
            )

            val converterFacade = converterFacadeTest(
                dataTimeout = dataTimeout,
                roundingWeight = 0.5,
                streamRateWarningPoint = 0.1,
                streamRateCriticalPoint = 0.15,
                reactionTime = 3000L,
                currentStateRepository = currentStateRepository
            )

            val context = converterBeContextTest(
                timeStart = timeStart,
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    slagRate = 0.16
                ),
                angles = ModelAngles(
                    angle = 66.0
                ),
                frame = ModelFrame(
                    frameTime = timeStart
                )
            )

            converterFacade.handleMath(context)

            delay(dataTimeout - 100L)

            assertEquals(1, context.eventList.size)
            var event = context.eventList.first()
            assertEquals(ModelEvent.Category.WARNING, event.category)
            assertEquals(ModelEvent.ExecutionStatus.NONE, event.executionStatus)
            assertTrue { event.isActive }
            assertEquals(ModelSignaler.ModelSignalerLevel.WARNING, context.signaler.level)
            assertEquals(ModelSignalerSound.ModelSignalerSoundType.NONE, context.signaler.sound.type)

            delay(1000L)

            assertEquals(1, context.eventList.size)
            event = context.eventList.first()
            assertEquals(ModelEvent.Category.WARNING, event.category)
            assertEquals(ModelEvent.ExecutionStatus.NONE, event.executionStatus)
            assertFalse { event.isActive }
            assertEquals(ModelAngles.NONE, context.angles)
            assertEquals(ModelSlagRate.NONE, context.slagRate)
            assertEquals(ModelSignaler.ModelSignalerLevel.NO_SIGNAL, context.signaler.level)
            assertEquals(ModelSignalerSound.ModelSignalerSoundType.NONE, context.signaler.sound.type)

        }
    }
}
