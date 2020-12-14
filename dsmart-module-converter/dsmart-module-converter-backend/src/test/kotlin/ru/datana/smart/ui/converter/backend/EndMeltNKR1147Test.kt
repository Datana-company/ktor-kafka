package ru.datana.smart.ui.converter.backend

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import ru.datana.smart.ui.converter.common.models.ModelEvent
import ru.datana.smart.ui.converter.common.models.ModelEventMode
import ru.datana.smart.ui.converter.common.models.ModelFrame
import ru.datana.smart.ui.converter.common.models.ModelSlagRate
import ru.datana.smart.ui.converter.repository.inmemory.EventRepositoryInMemory
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.Test

/**
 * NKR-1147
 * Во время окончания плавки генерируется ещё одна рекомендация (исправлено)
 */
internal class EndMeltNKR1147Test {

    /**
     * NKR-1147
     * В репозитории уже есть активная рекомендация.
     * После окончания плавки рекомендация записывается в историю.
     * Установлен режим рекомендаций по металлу (EVENT_MODE = STEEL).
     */
    @Test
    fun endMeltTestCase1() {
        runBlocking {
            val timeStart = Instant.now()
            val meltTimeout = 5000L

            val repository = createRepositoryWithEventForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_CRITICAL_EVENT,
                timeStart = timeStart.minusMillis(1000L),
                category = ModelEvent.Category.CRITICAL
            )

            val converterFacade = converterFacadeTest(
                roundingWeight = 0.1,
                meltTimeout = meltTimeout,
                currentState = createCurrentStateForTest(
                    lastAngle = 60.0,
                    avgSteelRate = 0.2
                ),
                converterRepository = repository
            )

            val context = converterBeContextTest(
                timeStart = timeStart,
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    steelRate = 0.1
                ),
                frame = ModelFrame(
                    frameTime = timeStart
                )
            )
            converterFacade.handleMath(context)
            delay(meltTimeout + 1000L)

            assertEquals(1, context.events.size)
            assertEquals(ModelEvent.Category.CRITICAL, context.events.first().category)
            assertEquals(false, context.events.first().isActive)
        }
    }

    /**
     * NKR-1147
     * Репозиторий пуст.
     * После окончания плавки создаётся рекомендация об успешной плавке.
     * Установлен режим рекомендаций по металлу (EVENT_MODE = STEEL).
     */
    @Test
    fun endMeltTestCase2() {
        runBlocking {
            val timeStart = Instant.now()
            val meltTimeout = 5000L

            val converterFacade = converterFacadeTest(
                roundingWeight = 0.1,
                meltTimeout = meltTimeout,
                currentState = createCurrentStateForTest(
                    lastAngle = 60.0,
                    avgSteelRate = 0.01
                )
            )

            val context = converterBeContextTest(
                timeStart = timeStart,
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    steelRate = 0.01
                ),
                frame = ModelFrame(
                    frameTime = timeStart
                )
            )
            converterFacade.handleMath(context)
            delay(meltTimeout + 1000L)

            assertEquals(1, context.events.size)
            assertEquals(ModelEvent.Category.INFO, context.events.first().category)
            assertEquals(false, context.events.first().isActive)
        }
    }

    /**
     * NKR-1147
     * В репозитории уже есть активная рекомендация.
     * После окончания плавки рекомендация записывается в историю.
     * Установлен режим рекомендаций по шлаку (EVENT_MODE = SLAG).
     */
    @Test
    fun endMeltTestCase3() {
        runBlocking {
            val timeStart = Instant.now()
            val meltTimeout = 5000L

            val repository = createRepositoryWithEventForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_CRITICAL_EVENT,
                timeStart = timeStart.minusMillis(1000L),
                category = ModelEvent.Category.CRITICAL
            )

            val converterFacade = converterFacadeTest(
                roundingWeight = 0.1,
                meltTimeout = meltTimeout,
                eventMode = ModelEventMode.SLAG,
                currentState = createCurrentStateForTest(
                    lastAngle = 60.0,
                    avgSlagRate = 0.2
                ),
                converterRepository = repository
            )

            val context = converterBeContextTest(
                timeStart = timeStart,
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    slagRate = 0.1
                ),
                frame = ModelFrame(
                    frameTime = timeStart
                )
            )
            converterFacade.handleMath(context)
            delay(meltTimeout + 1000L)

            assertEquals(1, context.events.size)
            assertEquals(ModelEvent.Category.CRITICAL, context.events.first().category)
            assertEquals(false, context.events.first().isActive)
        }
    }

    /**
     * NKR-1147
     * Репозиторий пуст.
     * После окончания плавки создаётся рекомендация об успешной плавке.
     * Установлен режим рекомендаций по шлаку (EVENT_MODE = SLAG).
     */
    @Test
    fun endMeltTestCase4() {
        runBlocking {
            val timeStart = Instant.now()
            val meltTimeout = 5000L

            val converterFacade = converterFacadeTest(
                roundingWeight = 0.1,
                meltTimeout = meltTimeout,
                eventMode = ModelEventMode.SLAG,
                currentState = createCurrentStateForTest(
                    lastAngle = 60.0,
                    avgSlagRate = 0.01
                )
            )

            val context = converterBeContextTest(
                timeStart = timeStart,
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    slagRate = 0.01
                ),
                frame = ModelFrame(
                    frameTime = timeStart
                )
            )
            converterFacade.handleMath(context)
            delay(meltTimeout + 1000L)

            assertEquals(1, context.events.size)
            assertEquals(ModelEvent.Category.INFO, context.events.first().category)
            assertEquals(false, context.events.first().isActive)
        }
    }
}
