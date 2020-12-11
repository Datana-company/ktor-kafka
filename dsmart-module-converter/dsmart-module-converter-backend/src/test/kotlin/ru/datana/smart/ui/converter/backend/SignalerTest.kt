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
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

@OptIn(ExperimentalTime::class)
internal class SignalerTest {

    // #1061 - Не успела выполниться Critical, содержание металла пришло в норму
    @Test
    fun signalerTestCase9() {
        runBlocking {
            val repository = EventRepositoryInMemory(ttl = 10.toDuration(DurationUnit.MINUTES))
            repository.create(ModelEvent(
                id = UUID.randomUUID().toString(),
                meltId = "211626-1606203458852",
                type = ModelEvent.EventType.STREAM_RATE_CRITICAL_EVENT,
                timeStart = Instant.now().minusMillis(1000L),
                timeFinish = Instant.now().minusMillis(1000L),
                metalRate = 0.16,
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
            val repository = EventRepositoryInMemory(ttl = 10.toDuration(DurationUnit.MINUTES))
            repository.create(ModelEvent(
                id = UUID.randomUUID().toString(),
                meltId = "211626-1606203458852",
                type = ModelEvent.EventType.STREAM_RATE_WARNING_EVENT,
                timeStart = Instant.now().minusMillis(1000L),
                timeFinish = Instant.now().minusMillis(1000L),
                metalRate = 0.11,
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
            val repository = EventRepositoryInMemory(ttl = 10.toDuration(DurationUnit.MINUTES))
            repository.create(ModelEvent(
                id = UUID.randomUUID().toString(),
                meltId = "211626-1606203458852",
                type = ModelEvent.EventType.STREAM_RATE_CRITICAL_EVENT,
                timeStart = Instant.now().minusMillis(3000L),
                timeFinish = Instant.now().minusMillis(1000L),
                metalRate = 0.16,
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
            val repository = EventRepositoryInMemory(ttl = 10.toDuration(DurationUnit.MINUTES))
            repository.create(ModelEvent(
                id = UUID.randomUUID().toString(),
                meltId = "211626-1606203458852",
                type = ModelEvent.EventType.STREAM_RATE_WARNING_EVENT,
                timeStart = Instant.now().minusMillis(3000L),
                timeFinish = Instant.now().minusMillis(1000L),
                metalRate = 0.11,
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
            val repository = EventRepositoryInMemory(ttl = 10.toDuration(DurationUnit.MINUTES))
            repository.create(ModelEvent(
                id = UUID.randomUUID().toString(),
                meltId = "211626-1606203458852",
                type = ModelEvent.EventType.STREAM_RATE_CRITICAL_EVENT,
                timeStart = Instant.now().minusMillis(3000L),
                timeFinish = Instant.now().minusMillis(1000L),
                metalRate = 0.16,
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
            val repository = EventRepositoryInMemory(ttl = 10.toDuration(DurationUnit.MINUTES))
            repository.create(ModelEvent(
                id = UUID.randomUUID().toString(),
                meltId = "211626-1606203458852",
                type = ModelEvent.EventType.STREAM_RATE_WARNING_EVENT,
                timeStart = Instant.now().minusMillis(3000L),
                timeFinish = Instant.now().minusMillis(1000L),
                metalRate = 0.11,
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
}
