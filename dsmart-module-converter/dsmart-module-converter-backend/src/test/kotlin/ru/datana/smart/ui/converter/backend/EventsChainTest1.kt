package ru.datana.smart.ui.converter.backend


import kotlinx.coroutines.runBlocking
import ru.datana.smart.ui.converter.common.models.*
import ru.datana.smart.ui.converter.common.utils.toPercent
import ru.datana.smart.ui.converter.repository.inmemory.EventRepositoryInMemory

import java.time.Instant
import kotlin.test.Test
import java.util.*
import java.util.concurrent.atomic.AtomicReference
import kotlin.test.BeforeTest
import kotlin.test.assertEquals

internal class EventsChainTest1 {
//    NKR-1031
//    Если есть активная рекомендация об изменении угла конвертера и при этом потери «металла»
//    сами по себе вернулись в пределы допустимой нормы, такая рекомендация должна становиться бледной,
//    независимо от того появилась ли сверху новая рекомендация или нет.

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
}

