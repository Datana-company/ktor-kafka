package ru.datana.smart.ui.converter.backend

import kotlinx.coroutines.runBlocking
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.*

import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

internal class EventsChainNKR1031Test {
    /**  NKR-1031
     *  Если есть активная рекомендация об изменении угла конвертера и при этом потери «металла»
     *  сами по себе вернулись в пределы допустимой нормы, такая рекомендация должна становиться бледной,
     *  независимо от того появилась ли сверху новая рекомендация или нет.
     */
    @Test
    fun isEventActiveNKR1031() {
        runBlocking {
            val timeStart = Instant.now()
            val repository = createRepositoryWithEventForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_CRITICAL_EVENT,
                timeStart = timeStart.minusMillis(1000L),
                angleStart = 66.0,
                category = ModelEvent.Category.CRITICAL
            )

            val stateRepository = createCurrentStateRepositoryForTest(
                lastAngle = 60.0,
                avgStreamRate = 0.16
            )

            val converterFacade = converterFacadeTest(
                roundingWeight = 0.5,
                currentStateRepository = stateRepository,
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
                    frameTime = timeStart
                )
            )
            converterFacade.handleMath(context)

            assertEquals(CorStatus.SUCCESS, context.status)
            assertEquals(ModelEvent.Category.CRITICAL, context.eventList.first().category)
            assertEquals(false, context.eventList.first().isActive)
            assertNotEquals(ModelEvent.Category.WARNING, context.eventList.first().category)
            assertNotEquals(true, context.eventList.first().isActive)
        }
    }
    /**  NKR-1031
     *  Если нет активная рекомендация об изменении угла конвертера и при этом потери «металла»
     *  сами по себе не вернулись в пределы допустимой нормы, такая рекомендация должна становиться активной
     */
    @Test
    fun isEventActiveNKR1031_WithFalseParameterTest() {
        runBlocking {
            val timeStart = Instant.now()
            val repository = createRepositoryWithEventForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_CRITICAL_EVENT,
                timeStart = timeStart.minusMillis(1000L),
                angleStart = 66.0,
                category = ModelEvent.Category.CRITICAL
            )

            val stateRepository = createCurrentStateRepositoryForTest(
                lastAngle = 66.0,
                avgStreamRate = 0.16
            )

            val converterFacade = converterFacadeTest(
                roundingWeight = 0.5,
                currentStateRepository = stateRepository,
                converterRepository = repository
            )

            val context = converterBeContextTest(
                timeStart = timeStart,
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    slagRate = 0.16,
                    steelRate = 0.16
                ),
                frame = ModelFrame(
                    frameTime = timeStart
                )
            )
            converterFacade.handleMath(context)

            assertEquals(CorStatus.SUCCESS, context.status)
            assertEquals(true, context.eventList.first().isActive)
            assertNotEquals(false, context.eventList.first().isActive)
        }
    }

}
