package ru.datana.smart.ui.converter.backend


import kotlinx.coroutines.runBlocking
import ru.datana.smart.ui.converter.common.models.*
import ru.datana.smart.ui.converter.common.utils.toPercent
import ru.datana.smart.ui.converter.repository.inmemory.EventRepositoryInMemory

import java.time.Instant
import kotlin.test.Test
import java.util.*
import java.util.concurrent.atomic.AtomicReference
import kotlin.test.assertEquals

internal class EventsChainTest1 {
    /**  NKR-1031
     *  Если есть активная рекомендация об изменении угла конвертера и при этом потери «металла»
     *  сами по себе вернулись в пределы допустимой нормы, такая рекомендация должна становиться бледной,
     *  независимо от того появилась ли сверху новая рекомендация или нет.
     */
    @Test
    fun isEventActive() {
        runBlocking {
            val repository = EventRepositoryInMemory()
            repository.create(
                ModelEvent(
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
                                  В потоке детектирован металл – ${toPercent(0.16)}%, процент потерь превышает критическое значение – ${toPercent(
                        0.15
                    )}%. Верните конвертер в вертикальное положение!
                                  """.trimIndent(),
                    category = ModelEvent.Category.CRITICAL
                )
            )

            val converterFacade = converterFacadeTest(
                roundingWeight = 0.5,
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
                    steelRate = 0.001

                ),
                frame = ModelFrame(
                    frameTime = Instant.now()
                )
            )

            converterFacade.handleMath(context)

            assertEquals(ModelEvent.Category.CRITICAL, context.events.first().category)
            assertEquals(false, context.events.first().isActive)
        }
    }

    /** NKR-1055  ModelEvent.ExecutionStatus.FAILED
     *  Предупреждение "Не выполнено" - т.к. истекло время реакции (3 сек), % металла не снизился, угол не уменьшился
     */
    @Test
    fun isExecutionStatusFAILED() {
        runBlocking {
            val repository = EventRepositoryInMemory()
            repository.create(ModelEvent(
                id = UUID.randomUUID().toString(),
                meltId = "211626-1606203458852",
                type = ModelEvent.EventType.STREAM_RATE_WARNING_EVENT,
                timeStart = Instant.now().minusMillis(5000L),
                timeFinish = Instant.now().minusMillis(1000L),
                metalRate = 0.011,
                warningPoint = 0.1,
                angleStart = 60.0,
                title = "Предупреждение",
                textMessage = """
                                  В потоке детектирован металл – ${toPercent(0.11)}% сверх допустимой нормы ${toPercent(0.1)}%. Верните конвертер в вертикальное положение.
                                  """.trimIndent(),
                category = ModelEvent.Category.WARNING
            ))
            val converterFacade = converterFacadeTest(
                roundingWeight = 0.1,
                metalRateWarningPoint = 0.1,
                metalRateCriticalPoint = 0.34,
                reactionTime = 3000,

                currentState = AtomicReference(
                    CurrentState(
                        currentMeltInfo = defaultMeltInfoTest(),
                        lastAngles = ModelAngles(
                            angle = 60.0
                        ),
                        avgSlagRate = ModelSlagRate(
                            steelRate = 0.011
                        )
                    )
                ),
                converterRepository = repository
            )
            val context = converterBeContextTest(
//            reactionTime = 3000L,
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    steelRate = 0.011
                ),
                frame = ModelFrame(
                    frameTime = Instant.now()
                ),
            )
            converterFacade.handleMath(context)
            assertEquals(ModelEvent.Category.WARNING, context.events.first().category)
            assertEquals(ModelEvent.ExecutionStatus.FAILED, context.events.first().executionStatus)

        }
    }

    /** NKR-1055  ModelEvent.ExecutionStatus.COMPLETED
     *  Предупреждение "Выполнено" - т.к. истекло время реакции (3 сек), % металла не снизился,
     *  угол уменьшился не менее, чем на 5 градусов
     */
    @Test
    fun isExecutionStatusCOMPLETED() {
        runBlocking {
            val repository = EventRepositoryInMemory()
            repository.create(ModelEvent(
                id = UUID.randomUUID().toString(),
                meltId = "211626-1606203458852",
                type = ModelEvent.EventType.STREAM_RATE_WARNING_EVENT,
                timeStart = Instant.now().minusMillis(5000L),
                timeFinish = Instant.now().minusMillis(1000L),
                metalRate = 0.011,
                warningPoint = 0.1,
                angleStart = 68.0,
                title = "Предупреждение",
                textMessage = """
                                  В потоке детектирован металл – ${toPercent(0.11)}% сверх допустимой нормы ${toPercent(0.1)}%. Верните конвертер в вертикальное положение.
                                  """.trimIndent(),
                category = ModelEvent.Category.WARNING
            ))
            val converterFacade = converterFacadeTest(
                roundingWeight = 0.1,
                metalRateWarningPoint = 0.1,
                metalRateCriticalPoint = 0.34,
                reactionTime = 3000,

                currentState = AtomicReference(
                    CurrentState(
                        currentMeltInfo = defaultMeltInfoTest(),
                        lastAngles = ModelAngles(
                            angle = 60.0
                        ),
                        avgSlagRate = ModelSlagRate(
                            steelRate = 0.011
                        )
                    )
                ),
                converterRepository = repository
            )
            val context = converterBeContextTest(
//            reactionTime = 3000L,
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    steelRate = 0.011
                ),
                frame = ModelFrame(
                    frameTime = Instant.now()
                ),
            )
            converterFacade.handleMath(context)
            assertEquals(ModelEvent.Category.WARNING, context.events.first().category)
            assertEquals(ModelEvent.ExecutionStatus.COMPLETED, context.events.first().executionStatus)

        }
    }


    /** NKR-1055  ModelEvent.ExecutionStatus.None
     * Предупреждение "без статуса" - т.к.  % металла снизился до допустимой нормы, а время реакции еще не истекло.
     */
    @Test
    fun isExecutionStatusNone() {
        runBlocking {
            val repository = EventRepositoryInMemory()
            repository.create(ModelEvent(
                id = UUID.randomUUID().toString(),
                meltId = "211626-1606203458852",
                type = ModelEvent.EventType.STREAM_RATE_WARNING_EVENT,
                timeStart = Instant.now().minusMillis(2000L),
                timeFinish = Instant.now().minusMillis(1000L),
                metalRate = 0.16,
                warningPoint = 0.1,
                angleStart = 68.0,
                title = "Предупреждение",
                textMessage = """
                                      В потоке детектирован металл – ${toPercent(0.16)}% сверх допустимой нормы ${toPercent(0.1)}%. Верните конвертер в вертикальное положение.
                                      """.trimIndent(),
                category = ModelEvent.Category.WARNING
            ))
            val converterFacade = converterFacadeTest(
                roundingWeight = 0.1,
                metalRateWarningPoint = 0.1,
                metalRateCriticalPoint = 0.34,
                reactionTime = 3000,

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
                    steelRate = 0.001
                ),
                frame = ModelFrame(
                    frameTime = Instant.now()
                ),
            )
            converterFacade.handleMath(context)
            assertEquals(ModelEvent.Category.WARNING, context.events.first().category)
            assertEquals(ModelEvent.ExecutionStatus.NONE, context.events.first().executionStatus)

        }
    }
    /**NKR-1041
     * ui-converter. По окончанию скачивания шлака последняя рекомендация и световой сигнал не меняют статус (остаются активными)
     */
    @Test
    fun isEventActiveAfterReactionTime() {
        runBlocking {
            val repository = EventRepositoryInMemory()
            repository.create(
                ModelEvent(
                    id = UUID.randomUUID().toString(),
                    meltId = "211626-1606203458852",
                    type = ModelEvent.EventType.STREAM_RATE_WARNING_EVENT,
                    timeStart = Instant.now().minusMillis(11000L),
                    timeFinish = Instant.now().minusMillis(1000L),
                    metalRate = 0.11,
                    criticalPoint = 0.15,
                    angleStart = 66.0,
                    title = "Критическая ситуация",
                    textMessage = """
                                  В потоке детектирован металл – ${toPercent(0.16)}%, процент потерь превышает критическое значение – ${toPercent(
                        0.15
                    )}%. Верните конвертер в вертикальное положение!
                                  """.trimIndent(),
                    category = ModelEvent.Category.WARNING
                )
            )

            val converterFacade = converterFacadeTest(

                roundingWeight = 0.1,
                metalRateWarningPoint = 0.1,
                metalRateCriticalPoint = 0.34,
                reactionTime = 3000,
                currentState = AtomicReference(
                    CurrentState(
                        currentMeltInfo = defaultMeltInfoTest(),
                        lastAngles = ModelAngles(
                            angle = 66.0
                        ),
                        lastSlagRate = ModelSlagRate(
                            steelRate = 0.14

                        )
                    )
                ),
                converterRepository = repository
            )

            val context = converterBeContextTest(
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    slagRate = 0.001,
                    steelRate = 0.001

                ),
                frame = ModelFrame(
                    frameTime = Instant.now()
                )
            )

            converterFacade.handleMath(context)

            assertEquals(ModelEvent.Category.WARNING, context.events.first().category)
            assertEquals(false, context.events.first().isActive)
        }
    }
}


