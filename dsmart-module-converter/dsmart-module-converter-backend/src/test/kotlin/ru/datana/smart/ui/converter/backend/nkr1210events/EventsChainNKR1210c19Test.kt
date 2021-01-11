package ru.datana.smart.ui.converter.backend.nkr1210events

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import ru.datana.smart.ui.converter.backend.*
import ru.datana.smart.ui.converter.common.models.*
import java.time.Instant
import kotlin.test.*

internal class EventsChainNKR1210c19Test {

    /**
     * NKR-1210
     * Проверка, что по истечении времени meltTimeout выдается информационное сообщение,
     * если % металла за время скачивания не был превышен
     */
    @Test
    fun `should show info event after meltTimeout if steelRate dont increase`(){
        runBlocking {
            val timeStart = Instant.now()
            val dataTimeout = 3000L
            val meltTimeout = 10000L

            val currentStateRepository = createCurrentStateRepositoryForTest(
                lastAngle = 66.0,
                avgSteelRate = 0.09
            )

            val converterFacade = converterFacadeTest(
                meltTimeout = meltTimeout,
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
                    steelRate = 0.09
                ),
                frame = ModelFrame(
                    frameTime = timeStart
                )
            )

            converterFacade.handleMath(context)

            assertEquals(0, context.eventList.size)
            delay(dataTimeout + 1000L)

            assertEquals(0, context.eventList.size)
            assertEquals(ModelAngles.NONE, context.angles)
            assertEquals(ModelFrame.NONE.copy(channel = ModelFrame.Channels.MATH), context.frame)

            delay(meltTimeout - dataTimeout)

            assertEquals(1, context.eventList.size)
            val event = context.eventList.first()
            assertEquals(ModelEvent.Category.INFO, event.category)
            assertEquals(ModelEvent.ExecutionStatus.NONE, event.executionStatus)
            assertFalse { event.isActive }

        }
    }

    @Test
    fun `should show info event after meltTimeout if slagRate dont increase`(){
        runBlocking {
            val timeStart = Instant.now()
            val dataTimeout = 3000L
            val meltTimeout = 10000L

            val currentStateRepository = createCurrentStateRepositoryForTest(
                lastAngle = 66.0,
                avgSlagRate = 0.09
            )

            val converterFacade = converterFacadeTest(
                meltTimeout = meltTimeout,
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
                    slagRate = 0.09
                ),
                frame = ModelFrame(
                    frameTime = timeStart
                )
            )

            converterFacade.handleMath(context)

            assertEquals(0, context.eventList.size)
            delay(dataTimeout + 1000L)

            assertEquals(0, context.eventList.size)
            assertEquals(ModelAngles.NONE, context.angles)
            assertEquals(ModelFrame.NONE.copy(channel = ModelFrame.Channels.MATH), context.frame)

            delay(meltTimeout - dataTimeout)

            assertEquals(1, context.eventList.size)
            val event = context.eventList.first()
            assertEquals(ModelEvent.Category.INFO, event.category)
            assertEquals(ModelEvent.ExecutionStatus.NONE, event.executionStatus)
            assertFalse { event.isActive }

        }
    }
}
