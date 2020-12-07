package ru.datana.smart.ui.converter.backend

import kotlinx.coroutines.runBlocking
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.*
import ru.datana.smart.ui.converter.common.utils.toPercent
import ru.datana.smart.ui.converter.repository.inmemory.EventRepositoryInMemory
import java.time.Instant
import java.util.*
import java.util.concurrent.atomic.AtomicReference
import kotlin.test.Test
import kotlin.test.assertEquals

internal class SignalerTest {

    // #1061 - Не успела выполниться Critical, содержание металла пришло в норму
    @Test
    fun signalerTestCase9() {
        runBlocking {
            val repository = EventRepositoryInMemory()
            repository.create(ModelEvent(
                id = UUID.randomUUID().toString(),
                meltId = "211626-1606203458852",
                type = ModelEvent.EventType.STREAM_RATE_CRITICAL_EVENT,
                timeStart = Instant.now().minusMillis(1000L),
                timeFinish = Instant.now().minusMillis(1000L),
                metalRate = 0.16,
                criticalPoint = 0.15,
                angleStart = 66.0,
                title = "Критическая ситуация",
                textMessage = """
                                  В потоке детектирован металл – ${toPercent(0.16)}%, процент потерь превышает критическое значение – ${toPercent(0.15)}%. Верните конвертер в вертикальное положение!
                                  """.trimIndent(),
                category = ModelEvent.Category.CRITICAL
            ))

            val converterFacade = converterFacadeTest(
                roundingWeight = 0.5,
                currentState = AtomicReference(
                    CurrentState(
                        currentMeltInfo = defaultMeltInfoTest(),
                        lastAngles = ModelAngles(
                            angle = 60.0
                        ),
                        avgSlagRate = ModelSlagRate(
                            steelRate = 0.16
                        )
                    )
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
    fun signalerTestCase10() {
        runBlocking {
            val repository = EventRepositoryInMemory()
            repository.create(ModelEvent(
                id = UUID.randomUUID().toString(),
                meltId = "211626-1606203458852",
                type = ModelEvent.EventType.STREAM_RATE_WARNING_EVENT,
                timeStart = Instant.now().minusMillis(1000L),
                timeFinish = Instant.now().minusMillis(1000L),
                metalRate = 0.11,
                warningPoint = 0.1,
                angleStart = 66.0,
                title = "Предупреждение",
                textMessage = """
                                  В потоке детектирован металл – ${toPercent(0.11)}% сверх допустимой нормы ${toPercent(0.1)}%. Верните конвертер в вертикальное положение.
                                  """.trimIndent(),
                category = ModelEvent.Category.WARNING
            ))
            val converterFacade = converterFacadeTest(
                currentState = AtomicReference(
                    CurrentState(
                        currentMeltInfo = defaultMeltInfoTest(),
                        lastAngles = ModelAngles(
                            angle = 60.0
                        ),
                        avgSlagRate = ModelSlagRate(
                            steelRate = 0.11
                        )
                    )
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
    fun signalerTestCase13() {
        runBlocking {
            val repository = EventRepositoryInMemory()
            repository.create(ModelEvent(
                id = UUID.randomUUID().toString(),
                meltId = "211626-1606203458852",
                type = ModelEvent.EventType.STREAM_RATE_CRITICAL_EVENT,
                timeStart = Instant.now().minusMillis(3000L),
                timeFinish = Instant.now().minusMillis(1000L),
                metalRate = 0.16,
                criticalPoint = 0.15,
                angleStart = 66.0,
                title = "Критическая ситуация",
                textMessage = """
                                  В потоке детектирован металл – ${toPercent(0.16)}%, процент потерь превышает критическое значение – ${toPercent(0.15)}%. Верните конвертер в вертикальное положение!
                                  """.trimIndent(),
                category = ModelEvent.Category.CRITICAL
            ))
            val converterFacade = converterFacadeTest(
                roundingWeight = 0.5,
                currentState = AtomicReference(
                    CurrentState(
                        currentMeltInfo = defaultMeltInfoTest(),
                        lastAngles = ModelAngles(
                            angle = 60.0
                        ),
                        avgSlagRate = ModelSlagRate(
                            steelRate = 0.16
                        )
                    )
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
    fun signalerTestCase14() {
        runBlocking {
            val repository = EventRepositoryInMemory()
            repository.create(ModelEvent(
                id = UUID.randomUUID().toString(),
                meltId = "211626-1606203458852",
                type = ModelEvent.EventType.STREAM_RATE_WARNING_EVENT,
                timeStart = Instant.now().minusMillis(3000L),
                timeFinish = Instant.now().minusMillis(1000L),
                metalRate = 0.11,
                warningPoint = 0.1,
                angleStart = 66.0,
                title = "Предупреждение",
                textMessage = """
                                  В потоке детектирован металл – ${toPercent(0.11)}% сверх допустимой нормы ${toPercent(0.1)}%. Верните конвертер в вертикальное положение.
                                  """.trimIndent(),
                category = ModelEvent.Category.WARNING
            ))
            val converterFacade = converterFacadeTest(
                currentState = AtomicReference(
                    CurrentState(
                        currentMeltInfo = defaultMeltInfoTest(),
                        lastAngles = ModelAngles(
                            angle = 60.0
                        ),
                        avgSlagRate = ModelSlagRate(
                            steelRate = 0.11
                        )
                    )
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
    fun signalerTestCase15() {
        runBlocking {
            val repository = EventRepositoryInMemory()
            repository.create(ModelEvent(
                id = UUID.randomUUID().toString(),
                meltId = "211626-1606203458852",
                type = ModelEvent.EventType.STREAM_RATE_CRITICAL_EVENT,
                timeStart = Instant.now().minusMillis(3000L),
                timeFinish = Instant.now().minusMillis(1000L),
                metalRate = 0.16,
                criticalPoint = 0.15,
                angleStart = 66.0,
                title = "Критическая ситуация",
                textMessage = """
                                  В потоке детектирован металл – ${toPercent(0.16)}%, процент потерь превышает критическое значение – ${toPercent(0.15)}%. Верните конвертер в вертикальное положение!
                                  """.trimIndent(),
                category = ModelEvent.Category.CRITICAL
            ))
            val converterFacade = converterFacadeTest(
                roundingWeight = 0.5,
                currentState = AtomicReference(
                    CurrentState(
                        currentMeltInfo = defaultMeltInfoTest(),
                        lastAngles = ModelAngles(
                            angle = 62.0
                        ),
                        avgSlagRate = ModelSlagRate(
                            steelRate = 0.16
                        )
                    )
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
    fun signalerTestCase16() {
        runBlocking {
            val repository = EventRepositoryInMemory()
            repository.create(ModelEvent(
                id = UUID.randomUUID().toString(),
                meltId = "211626-1606203458852",
                type = ModelEvent.EventType.STREAM_RATE_WARNING_EVENT,
                timeStart = Instant.now().minusMillis(3000L),
                timeFinish = Instant.now().minusMillis(1000L),
                metalRate = 0.11,
                warningPoint = 0.1,
                angleStart = 66.0,
                title = "Предупреждение",
                textMessage = """
                                  В потоке детектирован металл – ${toPercent(0.11)}% сверх допустимой нормы ${toPercent(0.1)}%. Верните конвертер в вертикальное положение.
                                  """.trimIndent(),
                category = ModelEvent.Category.WARNING
            ))
            val converterFacade = converterFacadeTest(
                currentState = AtomicReference(
                    CurrentState(
                        currentMeltInfo = defaultMeltInfoTest(),
                        lastAngles = ModelAngles(
                            angle = 62.0
                        ),
                        avgSlagRate = ModelSlagRate(
                            steelRate = 0.11
                        )
                    )
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
    fun isEventActiveAfterSirenLimitTime() {
        runBlocking {
            val repository = EventRepositoryInMemory()
            repository.create(
                ModelEvent(
                    id = UUID.randomUUID().toString(),
                    meltId = "211626-1606203458852",
                    type = ModelEvent.EventType.STREAM_RATE_CRITICAL_EVENT,
                    timeStart = Instant.now().minusMillis(11000L),
                    timeFinish = Instant.now().minusMillis(1000L),
                    metalRate = 0.16,
                    criticalPoint = 0.15,
                    angleStart = 66.0,
                    title = "Критическая ситуация",
                    textMessage = """
                                  В потоке детектирован металл – ${toPercent(0.16)}%, процент потерь превышает критическое значение – ${toPercent(
                        0.15
                    )}%. Верните конвертер в вертикальное положение!
                                  """.trimIndent(),
                    category = ModelEvent.Category.CRITICAL
                )
            )

            val converterFacade = converterFacadeTest(

                roundingWeight = 0.1,
                metalRateWarningPoint = 0.1,
                metalRateCriticalPoint = 0.34,
                reactionTime = 3000,
                sirenLimitTime = 3000,
                currentState = AtomicReference(
                    CurrentState(
                        currentMeltInfo = defaultMeltInfoTest(),
                        lastAngles = ModelAngles(
                            angle = 66.0
                        ),
                        lastSlagRate = ModelSlagRate(
                            steelRate = 0.16

                        )
                    )
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
