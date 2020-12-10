package ru.datana.smart.ui.converter.backend

import kotlinx.coroutines.runBlocking
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.*
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals

internal class SignalerTest {

    // #1061 - Не успела выполниться Critical, содержание металла пришло в норму
    @Test
    fun signalerTestCase9NKR1061() {
        runBlocking {
            val repository = createRepositoryWithEventForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_CRITICAL_EVENT,
                timeStart = Instant.now().minusMillis(1000L),
                metalRate = 0.16,
                criticalPoint = 0.15,
                warningPoint = null,
                angleStart = 66.0,
                category = ModelEvent.Category.CRITICAL
            )

            val converterFacade = converterFacadeTest(
                roundingWeight = 0.5,
                currentState = createCurrentStateForTest(
                    lastAngleTime = null,
                    lastAngle = 60.0,
                    lastSource = null,
                    lastSteelRate = 0.16,
                    lastSlagRate = null
                ),
                converterRepository = repository
            )
            val context = converterBeContextTest(
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    slagRate = 0.0,
                    steelRate = 0.0
                ),
                frame = ModelFrame(
                    frameTime = Instant.now()
                )
            )
            converterFacade.handleMath(context)

            assertEquals(CorStatus.SUCCESS, context.status)
            assertEquals(SignalerModel.SignalerLevelModel.NO_SIGNAL, context.signaler.level)
            assertEquals(SignalerSoundModel.NONE, context.signaler.sound)
        }
    }

    // #1061 - Не успела выполниться Warning, содержание металла пришло в норму
    @Test
    fun signalerTestCase10NKR1061() {
        runBlocking {
            val repository = createRepositoryWithEventForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_WARNING_EVENT,
                timeStart = Instant.now().minusMillis(1000L),
                metalRate = 0.11,
                criticalPoint = null,
                warningPoint = 0.1,
                angleStart = 66.0,
                category = ModelEvent.Category.WARNING
            )

            val converterFacade = converterFacadeTest(
                currentState = createCurrentStateForTest(
                    lastAngleTime = null,
                    lastAngle = 60.0,
                    lastSource = null,
                    lastSteelRate = 0.11,
                    lastSlagRate = null
                ),
                converterRepository = repository
            )
            val context = converterBeContextTest(
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    slagRate = 0.0,
                    steelRate = 0.0
                ),
                frame = ModelFrame(
                    frameTime = Instant.now()
                )
            )
            converterFacade.handleMath(context)

            assertEquals(CorStatus.SUCCESS, context.status)
            assertEquals(SignalerModel.SignalerLevelModel.NO_SIGNAL, context.signaler.level)
            assertEquals(SignalerSoundModel.NONE, context.signaler.sound)
        }
    }

    // #1061 - Успела выполниться Critical (статус Выполнено), содержание металла пришло в норму
    @Test
    fun signalerTestCase13NKR1061() {
        runBlocking {
            val repository = createRepositoryWithEventForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_CRITICAL_EVENT,
                timeStart = Instant.now().minusMillis(3000L),
                metalRate = 0.16,
                criticalPoint = 0.15,
                warningPoint = null,
                angleStart = 66.0,
                category = ModelEvent.Category.CRITICAL
            )

            val converterFacade = converterFacadeTest(
                roundingWeight = 0.5,
                currentState = createCurrentStateForTest(
                    lastAngleTime = null,
                    lastAngle = 60.0,
                    lastSource = null,
                    lastSteelRate = 0.16,
                    lastSlagRate = null
                ),
                converterRepository = repository
            )
            val context = converterBeContextTest(
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    slagRate = 0.0,
                    steelRate = 0.0
                ),
                frame = ModelFrame(
                    frameTime = Instant.now()
                )
            )
            converterFacade.handleMath(context)

            assertEquals(CorStatus.SUCCESS, context.status)
            assertEquals(SignalerModel.SignalerLevelModel.NO_SIGNAL, context.signaler.level)
            assertEquals(SignalerSoundModel.NONE, context.signaler.sound)
        }
    }

    // #1061 - Успела выполниться Warning (статус Выполнено), содержание металла пришло в норму
    @Test
    fun signalerTestCase14NKR1061() {
        runBlocking {
            val repository = createRepositoryWithEventForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_WARNING_EVENT,
                timeStart = Instant.now().minusMillis(3000L),
                metalRate = 0.11,
                criticalPoint = null,
                warningPoint = 0.1,
                angleStart = 66.0,
                category = ModelEvent.Category.WARNING
            )
            val converterFacade = converterFacadeTest(
                currentState = createCurrentStateForTest(
                    lastAngleTime = null,
                    lastAngle = 60.0,
                    lastSource = null,
                    lastSteelRate = 0.11,
                    lastSlagRate = null
                ),
                converterRepository = repository
            )
            val context = converterBeContextTest(
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    slagRate = 0.0,
                    steelRate = 0.0
                ),
                frame = ModelFrame(
                    frameTime = Instant.now()
                )
            )
            converterFacade.handleMath(context)

            assertEquals(CorStatus.SUCCESS, context.status)
            assertEquals(SignalerModel.SignalerLevelModel.NO_SIGNAL, context.signaler.level)
            assertEquals(SignalerSoundModel.NONE, context.signaler.sound)
        }
    }

    // #1061 - Успела выполниться Critical (статус Не выполнено), содержание металла пришло в норму
    @Test
    fun signalerTestCase15NKR1061() {
        runBlocking {
            val repository = createRepositoryWithEventForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_CRITICAL_EVENT,
                timeStart = Instant.now().minusMillis(3000L),
                metalRate = 0.16,
                criticalPoint = 0.15,
                warningPoint = null,
                angleStart = 66.0,
                category = ModelEvent.Category.CRITICAL
            )

            val converterFacade = converterFacadeTest(
                roundingWeight = 0.5,
                currentState = createCurrentStateForTest(
                    lastAngleTime = null,
                    lastAngle = 62.0,
                    lastSource = null,
                    lastSteelRate = 0.16,
                    lastSlagRate = null
                ),
                converterRepository = repository
            )
            val context = converterBeContextTest(
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    slagRate = 0.0,
                    steelRate = 0.0
                ),
                frame = ModelFrame(
                    frameTime = Instant.now()
                )
            )
            converterFacade.handleMath(context)

            assertEquals(CorStatus.SUCCESS, context.status)
            assertEquals(SignalerModel.SignalerLevelModel.NO_SIGNAL, context.signaler.level)
            assertEquals(SignalerSoundModel.NONE, context.signaler.sound)
        }
    }

    // #1061 - Успела выполниться Warning (статус Не выполнено), содержание металла пришло в норму
    @Test
    fun signalerTestCase16NKR1061() {
        runBlocking {
            val repository = createRepositoryWithEventForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_WARNING_EVENT,
                timeStart = Instant.now().minusMillis(3000L),
                metalRate = 0.11,
                criticalPoint = null,
                warningPoint = 0.1,
                angleStart = 66.0,
                category = ModelEvent.Category.WARNING
            )

            val converterFacade = converterFacadeTest(
                currentState = createCurrentStateForTest(
                    lastAngleTime = null,
                    lastAngle = 62.0,
                    lastSource = null,
                    lastSteelRate = 0.16,
                    lastSlagRate = null
                ),
                converterRepository = repository
            )
            val context = converterBeContextTest(
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    slagRate = 0.0,
                    steelRate = 0.0
                ),
                frame = ModelFrame(
                    frameTime = Instant.now()
                )
            )
            converterFacade.handleMath(context)

            assertEquals(CorStatus.SUCCESS, context.status)
            assertEquals(SignalerModel.SignalerLevelModel.NO_SIGNAL, context.signaler.level)
            assertEquals(SignalerSoundModel.NONE, context.signaler.sound)
        }
    }

    /**NKR-905
     * [лимит на звуковой сигнал сирены] - в секундах - сколько по длительности должен продолжаться звуковой сигнал
     * [время реакции на рекомендацию] - REACTION_TIME   - Уже проверено в EventsChainTest
     * [допустимый % потери] - METAL_RATE_POINT_WARNING - Уже проверено в EventsChainTest
     * [% критической потери металла] - METAL_RATE_POINT_CRITICAL - Уже проверено в EventsChainTest
     */
    @Test
    fun isEventActiveAfterSirenLimitTimeNKR905() {
        runBlocking {
            val repository = createRepositoryWithEventForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_CRITICAL_EVENT,
                timeStart = Instant.now().minusMillis(3000L),
                metalRate = 0.16,
                criticalPoint = 0.15,
                warningPoint = null,
                angleStart = 66.0,
                category = ModelEvent.Category.CRITICAL
            )

            val converterFacade = converterFacadeTest(

                roundingWeight = 0.1,
                metalRateWarningPoint = 0.1,
                metalRateCriticalPoint = 0.34,
                reactionTime = 3000,
                sirenLimitTime = 3000,
                currentState = createCurrentStateForTest(
                    lastAngleTime = null,
                    lastAngle = 66.0,
                    lastSource = null,
                    lastSteelRate = 0.16,
                    lastSlagRate = null
                ),
                converterRepository = repository
            )

            val context = converterBeContextTest(
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    slagRate = 0.001,
                    steelRate = 0.16

                ),
                frame = ModelFrame(
                    frameTime = Instant.now()
                )
            )

            converterFacade.handleMath(context)
            assertEquals(3000L, context.sirenLimitTime)
        }
    }
}
