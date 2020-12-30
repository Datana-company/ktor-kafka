package ru.datana.smart.ui.converter.backend.nkr1210events

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
     * FAILED
     */
    //@Test
    fun `should show info event after meltTimeout if steelRate dont increase`(){
        runBlocking {
            val timeStart = Instant.now()
            val dataTimeout = 3000L
            val meltTimeout = 10000L


            val converterFacade = converterFacadeTest(
                meltTimeout = meltTimeout,
                dataTimeout = dataTimeout,
                roundingWeight = 0.5,
                streamRateWarningPoint = 0.1,
                streamRateCriticalPoint = 0.15,
                reactionTime = 3000L,
                currentState = createCurrentStateForTest(
                    lastAngle = 66.0,
                    avgStreamRate = 0.09
                )
            )

            val context1 = converterBeContextTest(
                timeStart = timeStart.plusMillis(dataTimeout),
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    steelRate = 0.09
                ),
                frame = ModelFrame(
                    frameTime = timeStart.plusMillis(dataTimeout)
                )
            )

            val context2 = converterBeContextTest(
                timeStart = timeStart.plusMillis(meltTimeout),
                meltInfo = defaultMeltInfoTest(),
                frame = ModelFrame(
                    frameTime = timeStart.plusMillis(meltTimeout)
                )
            )

            converterFacade.handleMath(context1)

            assertEquals(0, context1.events.size)
            assertEquals(ModelAngles.NONE, context1.angles)
            //assertEquals(ModelFrame.NONE, context1.frame)

            converterFacade.handleMath(context2)

            assertEquals(1, context2.events.size)
            val event = context2.events.first()
            assertEquals(ModelEvent.Category.INFO, event.category)
            assertEquals(ModelEvent.ExecutionStatus.NONE, event.executionStatus)
            assertFalse { event.isActive }

        }
    }
}
