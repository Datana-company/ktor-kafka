package ru.datana.smart.ui.converter.backend

import kotlinx.coroutines.runBlocking
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.*
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

internal class SignalerNKR1061Test {
    /** NKR-1061  Не успела выполниться Critical, содержание металла пришло в норму*/
    @Test
    fun signalerTestCase1NKR1061() {
        runBlocking {
            val timeStart = Instant.now()

            val repository = createEventRepositoryForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_CRITICAL_EVENT,
                timeStart = timeStart.minusMillis(1000L),
                angleStart = 66.0,
                category = ModelEvent.Category.CRITICAL
            )

            val stateRepository = createCurrentStateRepositoryForTest(
                lastAngle = 60.0,
                lastSteelRate = 0.16,
                lastAvgSlagRate = 0.16
            )

            val converterFacade = converterFacadeTest(
                roundingWeight = 0.5,
                currentStateRepository = stateRepository,
                eventRepository = repository
            )
            val context = converterBeContextTest(
                timeStart = timeStart,
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    slagRate = 0.0,
                    steelRate = 0.0
                ),
                frame = ModelFrame(
                    frameTime = timeStart
                )
            )
            converterFacade.handleMath(context)

            assertEquals(CorStatus.SUCCESS, context.status)
            assertEquals(ModelSignaler.ModelSignalerLevel.NO_SIGNAL, context.signaler.level)
            assertEquals(ModelSignalerSound.NONE, context.signaler.sound)
        }
    }

    /** NKR-1061  Успела выполниться Critical (статус  Выполнено), содержание металла не пришло в норму */
    @Test
    fun signalerTestCase9NKR1061WithFalseParameterTest() {
        runBlocking {
            val repository = createEventRepositoryForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_CRITICAL_EVENT,
                timeStart = Instant.now().minusMillis(3000L),
                angleStart = 66.0,
                category = ModelEvent.Category.CRITICAL
            )

            val stateRepository = createCurrentStateRepositoryForTest(
                lastAngle = 60.0,
                lastSteelRate = 0.16,
                lastAvgSlagRate = 0.16
            )

            val converterFacade = converterFacadeTest(
                roundingWeight = 0.5,
                currentStateRepository = stateRepository,
                eventRepository = repository
            )
            val context = converterBeContextTest(
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    slagRate = 0.0,
                    steelRate = 0.16
                ),
                frame = ModelFrame(
                    frameTime = Instant.now()
                )
            )
            converterFacade.handleMath(context)

            assertEquals(CorStatus.SUCCESS, context.status)
            assertNotEquals(ModelSignaler.ModelSignalerLevel.NO_SIGNAL, context.signaler.level)
            assertNotEquals(ModelSignalerSound.NONE, context.signaler.sound)
            assertEquals(ModelSignaler.ModelSignalerLevel.CRITICAL, context.signaler.level)
            assertEquals(ModelSignalerSound.ModelSignalerSoundType.SOUND_1, context.signaler.sound.type)
        }
    }

    /** NKR-1061 Не успела выполниться Warning, содержание металла пришло в норму */
    @Test
    fun signalerTestCase2NKR1061() {
        runBlocking {
            val timeStart = Instant.now()

            val repository = createEventRepositoryForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_WARNING_EVENT,
                timeStart = timeStart.minusMillis(1000L),
                angleStart = 66.0,
                category = ModelEvent.Category.WARNING
            )

            val stateRepository = createCurrentStateRepositoryForTest(
                lastAngle = 60.0,
                lastAvgSlagRate = 0.11
            )

            val converterFacade = converterFacadeTest(
                currentStateRepository = stateRepository,
                eventRepository = repository
            )
            val context = converterBeContextTest(
                timeStart = timeStart,
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    slagRate = 0.0,
                    steelRate = 0.0
                ),
                frame = ModelFrame(
                    frameTime = timeStart
                )
            )
            converterFacade.handleMath(context)

            assertEquals(CorStatus.SUCCESS, context.status)
            assertEquals(ModelSignaler.ModelSignalerLevel.NO_SIGNAL, context.signaler.level)
            assertEquals(ModelSignalerSound.NONE, context.signaler.sound)
        }
    }

    /** NKR-1061   успела выполниться Warning, содержание металла Не пришло в норму*/
    @Test
    fun signalerTestCase10NKR1061WithFalseParameterTest() {
        runBlocking {
            val repository = createEventRepositoryForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_WARNING_EVENT,
                timeStart = Instant.now().minusMillis(4000L),
                angleStart = 66.0,
                category = ModelEvent.Category.WARNING
            )

            val stateRepository = createCurrentStateRepositoryForTest(
                lastAngle = 60.0,
                lastAvgSlagRate = 0.13,
                lastSteelRate = 0.13
            )

            val converterFacade = converterFacadeTest(
                roundingWeight = 0.5,
                currentStateRepository = stateRepository,
                eventRepository = repository
            )
            val context = converterBeContextTest(
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    slagRate = 0.0,
                    steelRate = 0.11
                ),
                frame = ModelFrame(
                    frameTime = Instant.now()
                )
            )
            converterFacade.handleMath(context)

            assertEquals(CorStatus.SUCCESS, context.status)
            assertNotEquals(ModelSignalerSound.ModelSignalerSoundType.SOUND_1, context.signaler.sound.type)
            assertEquals(ModelSignalerSound.NONE, context.signaler.sound)
            assertNotEquals(ModelSignaler.ModelSignalerLevel.NO_SIGNAL, context.signaler.level)
            assertEquals(ModelSignaler.ModelSignalerLevel.WARNING, context.signaler.level)
        }
    }

    /** NKR-1061 Успела выполниться Critical (статус Выполнено), содержание металла пришло в норму */
    @Test
    fun signalerTestCase3NKR1061() {
        runBlocking {
            val timeStart = Instant.now()

            val repository = createEventRepositoryForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_CRITICAL_EVENT,
                timeStart = timeStart.minusMillis(3000L),
                angleStart = 66.0,
                category = ModelEvent.Category.CRITICAL
            )

            val stateRepository = createCurrentStateRepositoryForTest(
                lastAngle = 60.0,
                lastAvgSlagRate = 0.16
            )

            val converterFacade = converterFacadeTest(
                roundingWeight = 0.5,
                currentStateRepository = stateRepository,
                eventRepository = repository
            )
            val context = converterBeContextTest(
                timeStart = timeStart,
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    slagRate = 0.0,
                    steelRate = 0.0
                ),
                frame = ModelFrame(
                    frameTime = timeStart
                )
            )
            converterFacade.handleMath(context)

            assertEquals(CorStatus.SUCCESS, context.status)
            assertEquals(ModelSignaler.ModelSignalerLevel.NO_SIGNAL, context.signaler.level)
            assertEquals(ModelSignalerSound.NONE, context.signaler.sound)
        }
    }

    /** NKR-1061 Успела выполниться Critical (статус не Выполнено), содержание металла не пришло в норму */
    @Test
    fun signalerTestCase13NKR1061WithFalseParameterTest() {
        runBlocking {
            val repository = createEventRepositoryForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_CRITICAL_EVENT,
                timeStart = Instant.now().minusMillis(3000L),
                angleStart = 66.0,
                category = ModelEvent.Category.CRITICAL
            )

            val stateRepository = createCurrentStateRepositoryForTest(
                lastAngle = 66.0,
                lastAvgSlagRate = 0.16
            )

            val converterFacade = converterFacadeTest(
                roundingWeight = 0.5,
                currentStateRepository = stateRepository,
                eventRepository = repository
            )
            val context = converterBeContextTest(
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    slagRate = 0.0,
                    steelRate = 0.16
                ),
                frame = ModelFrame(
                    frameTime = Instant.now()
                )
            )
            converterFacade.handleMath(context)

            assertEquals(CorStatus.SUCCESS, context.status)
            assertNotEquals(ModelSignaler.ModelSignalerLevel.NO_SIGNAL, context.signaler.level)
            assertEquals(ModelSignaler.ModelSignalerLevel.CRITICAL, context.signaler.level)
            assertNotEquals(ModelSignalerSound.NONE, context.signaler.sound)
            assertEquals(ModelSignalerSound.ModelSignalerSoundType.SOUND_1, context.signaler.sound.type)
        }
    }

    /** NKR-1061 Успела выполниться Warning (статус Выполнено), содержание металла пришло в норму */
    @Test
    fun signalerTestCase4NKR1061() {
        runBlocking {
            val timeStart = Instant.now()

            val repository = createEventRepositoryForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_WARNING_EVENT,
                timeStart = timeStart.minusMillis(3000L),
                angleStart = 66.0,
                category = ModelEvent.Category.WARNING
            )

            val stateRepository = createCurrentStateRepositoryForTest(
                lastAngle = 60.0,
                lastAvgSlagRate = 0.11
            )

            val converterFacade = converterFacadeTest(
                currentStateRepository = stateRepository,
                eventRepository = repository
            )
            val context = converterBeContextTest(
                timeStart = timeStart,
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    slagRate = 0.0,
                    steelRate = 0.0
                ),
                frame = ModelFrame(
                    frameTime = timeStart
                )
            )
            converterFacade.handleMath(context)

            assertEquals(CorStatus.SUCCESS, context.status)
            assertEquals(ModelSignaler.ModelSignalerLevel.NO_SIGNAL, context.signaler.level)
            assertEquals(ModelSignalerSound.NONE, context.signaler.sound)
        }
    }

    /** NKR-1061 Успела выполниться Warning (статус не Выполнено), содержание металла нe пришло в норму*/
    @Test
    fun signalerTestCase14NKR1061WithFalseParameterTest() {
        runBlocking {
            val repository = createEventRepositoryForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_WARNING_EVENT,
                timeStart = Instant.now().minusMillis(3000L),
                angleStart = 66.0,
                category = ModelEvent.Category.WARNING
            )

            val stateRepository = createCurrentStateRepositoryForTest(
                lastAngle = 66.0,
                lastAvgSlagRate = 0.11
            )

            val converterFacade = converterFacadeTest(
                currentStateRepository = stateRepository,
                eventRepository = repository
            )
            val context = converterBeContextTest(
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    slagRate = 0.0,
                    steelRate = 0.11
                ),
                frame = ModelFrame(
                    frameTime = Instant.now()
                )
            )

            converterFacade.handleMath(context)

            assertEquals(CorStatus.SUCCESS, context.status)
            assertNotEquals(ModelSignaler.ModelSignalerLevel.NO_SIGNAL, context.signaler.level)
            assertEquals(ModelSignaler.ModelSignalerLevel.WARNING, context.signaler.level)
            assertNotEquals(ModelSignalerSound.ModelSignalerSoundType.SOUND_1, context.signaler.sound.type)
            assertEquals(ModelSignalerSound.NONE, context.signaler.sound)
        }
    }

    /** NKR-1061 - Успела выполниться Critical (статус Не выполнено), содержание металла пришло в норму */
    @Test
    fun signalerTestCase5NKR1061() {
        runBlocking {
            val timeStart = Instant.now()

            val repository = createEventRepositoryForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_CRITICAL_EVENT,
                timeStart = timeStart.minusMillis(3000L),
                angleStart = 66.0,
                category = ModelEvent.Category.CRITICAL
            )

            val stateRepository = createCurrentStateRepositoryForTest(
                lastAngle = 62.0,
                lastAvgSlagRate = 0.16
            )

            val converterFacade = converterFacadeTest(
                roundingWeight = 0.5,
                currentStateRepository = stateRepository,
                eventRepository = repository
            )
            val context = converterBeContextTest(
                timeStart = timeStart,
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    slagRate = 0.0,
                    steelRate = 0.0
                ),
                frame = ModelFrame(
                    frameTime = timeStart
                )
            )
            converterFacade.handleMath(context)

            assertEquals(CorStatus.SUCCESS, context.status)
            assertEquals(ModelSignaler.ModelSignalerLevel.NO_SIGNAL, context.signaler.level)
            assertEquals(ModelSignalerSound.NONE, context.signaler.sound)
        }
    }

    /** NKR-1061 Успела выполниться Critical (статус выполнено), содержание металла Не пришло в норму*/
    @Test
    fun signalerTestCase15NKR1061_WithFalseParameterTest() {
        runBlocking {
            val repository = createEventRepositoryForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_CRITICAL_EVENT,
                timeStart = Instant.now().minusMillis(3000L),
                angleStart = 66.0,
                category = ModelEvent.Category.CRITICAL
            )

            val stateRepository = createCurrentStateRepositoryForTest(
                lastAngle = 60.0,
                lastAvgSlagRate = 0.16
            )

            val converterFacade = converterFacadeTest(
                roundingWeight = 0.5,
                currentStateRepository = stateRepository,
                eventRepository = repository
            )
            val context = converterBeContextTest(
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    slagRate = 0.0,
                    steelRate = 0.16
                ),
                frame = ModelFrame(
                    frameTime = Instant.now()
                )
            )
            converterFacade.handleMath(context)

            assertEquals(CorStatus.SUCCESS, context.status)
            assertNotEquals(ModelSignaler.ModelSignalerLevel.NO_SIGNAL, context.signaler.level)
            assertNotEquals(ModelSignalerSound.NONE, context.signaler.sound)
            assertNotEquals(ModelSignalerSound.ModelSignalerSoundType.NONE, context.signaler.sound.type)
            assertEquals(ModelSignalerSound.ModelSignalerSoundType.SOUND_1, context.signaler.sound.type)
        }
    }

    /** NKR-1061 Успела выполниться Warning (статус Не выполнено), содержание металла пришло в норму */
    @Test
    fun signalerTestCase6NKR1061() {
        runBlocking {
            val timeStart = Instant.now()

            val repository = createEventRepositoryForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_WARNING_EVENT,
                timeStart = timeStart.minusMillis(3000L),
                angleStart = 66.0,
                category = ModelEvent.Category.WARNING
            )

            val stateRepository = createCurrentStateRepositoryForTest(
                lastAngle = 62.0,
                lastAvgSlagRate = 0.11
            )

            val converterFacade = converterFacadeTest(
                currentStateRepository = stateRepository,
                eventRepository = repository
            )
            val context = converterBeContextTest(
                timeStart = timeStart,
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    slagRate = 0.0,
                    steelRate = 0.0
                ),
                frame = ModelFrame(
                    frameTime = timeStart
                )
            )
            converterFacade.handleMath(context)

            assertEquals(CorStatus.SUCCESS, context.status)
            assertEquals(ModelSignaler.ModelSignalerLevel.NO_SIGNAL, context.signaler.level)
            assertEquals(ModelSignalerSound.NONE, context.signaler.sound)
        }
    }

    /** NKR-1061 Успела выполниться Warning (статус выполнено), содержание металла Не пришло в норму
     * но перешло в статус Критично (Critical)
     */
    @Test
    fun signalerTestCase16NKR1061_WithFalseParameterTest() {
        runBlocking {
            val timeStart = Instant.now()
            val repository = createEventRepositoryForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_WARNING_EVENT,
                timeStart = timeStart.minusMillis(3000L),
                angleStart = 66.0,
                category = ModelEvent.Category.WARNING
            )

            val stateRepository = createCurrentStateRepositoryForTest(
                lastAngle = 60.0,
                lastAvgSlagRate = 0.16
            )

            val converterFacade = converterFacadeTest(
                roundingWeight = 0.1,
                streamRateWarningPoint = 0.1,
                reactionTime = 3000,
                sirenLimitTime = 3000,
                currentStateRepository = stateRepository,
                eventRepository = repository
            )
            val context = converterBeContextTest(
                timeStart = timeStart,
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    slagRate = 0.11,
                    steelRate = 0.17
                ),
                frame = ModelFrame(
                    frameTime = timeStart
                )
            )
            converterFacade.handleMath(context)

            assertEquals(CorStatus.SUCCESS, context.status)
            assertEquals(ModelSignaler.ModelSignalerLevel.CRITICAL, context.signaler.level)
            assertNotEquals(ModelSignalerSound.ModelSignalerSoundType.NONE, context.signaler.sound.type)
            assertNotEquals(ModelSignalerSound.ModelSignalerSoundType.NONE, context.signaler.sound.type)
            assertEquals(ModelSignalerSound.ModelSignalerSoundType.SOUND_1, context.signaler.sound.type)

        }
    }
}
