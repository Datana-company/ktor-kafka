package ru.datana.smart.ui.converter.backend

import kotlinx.coroutines.runBlocking
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.events.MetalRateCriticalEvent
import ru.datana.smart.ui.converter.common.events.MetalRateWarningEvent
import ru.datana.smart.ui.converter.common.models.*
import ru.datana.smart.ui.converter.repository.inmemory.UserEventRepositoryInMemory
import java.time.Instant
import java.util.*
import java.util.concurrent.atomic.AtomicReference
import kotlin.test.Test
import kotlin.test.assertEquals

internal class SignalerTest {

    // #1061 - Не успела выполниться Critical, содержание металла пришло в норму
    @Test
    fun signalerTestCase9() {
        val repository = UserEventRepositoryInMemory()
        repository.put("211626-1606203458852", MetalRateCriticalEvent(
            id = UUID.randomUUID().toString(),
            timeStart = Instant.now().minusMillis(1000L),
            timeFinish = Instant.now().minusMillis(1000L),
            metalRate = 0.16,
            criticalPoint = 0.15,
            angleStart = 66.0
        ))
        val converterFacade = converterFacadeTest(
            roundingWeight = 0.5,
            currentState = AtomicReference(
                CurrentState(
                    currentMeltInfo = defaultMeltInfoTest(),
                    lastAngles = ModelAngles(
                        angle = 60.0
                    ),
                    lastSlagRate = ModelSlagRate(
                        avgSteelRate = 0.16
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

        runBlocking {
            converterFacade.handleMath(context)
        }

        assertEquals(CorStatus.SUCCESS, context.status)
        assertEquals(SignalerModel.SignalerLevelModel.NO_SIGNAL, context.signaler.level)
        assertEquals(SignalerSoundModel.NONE, context.signaler.sound)
    }

    // #1061 - Не успела выполниться Warning, содержание металла пришло в норму
    @Test
    fun signalerTestCase10() {
        val repository = UserEventRepositoryInMemory()
        repository.put("211626-1606203458852", MetalRateWarningEvent(
            id = UUID.randomUUID().toString(),
            timeStart = Instant.now().minusMillis(1000L),
            timeFinish = Instant.now().minusMillis(1000L),
            metalRate = 0.11,
            warningPoint = 0.1,
            angleStart = 66.0
        ))
        val converterFacade = converterFacadeTest(
            currentState = AtomicReference(
                CurrentState(
                    currentMeltInfo = defaultMeltInfoTest(),
                    lastAngles = ModelAngles(
                        angle = 60.0
                    ),
                    lastSlagRate = ModelSlagRate(
                        avgSteelRate = 0.11
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

        runBlocking {
            converterFacade.handleMath(context)
        }

        assertEquals(CorStatus.SUCCESS, context.status)
        assertEquals(SignalerModel.SignalerLevelModel.NO_SIGNAL, context.signaler.level)
        assertEquals(SignalerSoundModel.NONE, context.signaler.sound)
    }

    // #1061 - Успела выполниться Critical (статус Выполнено), содержание металла пришло в норму
    @Test
    fun signalerTestCase13() {
        val repository = UserEventRepositoryInMemory()
        repository.put("211626-1606203458852", MetalRateCriticalEvent(
            id = UUID.randomUUID().toString(),
            timeStart = Instant.now().minusMillis(3000L),
            timeFinish = Instant.now().minusMillis(1000L),
            metalRate = 0.16,
            criticalPoint = 0.15,
            angleStart = 66.0
        ))
        val converterFacade = converterFacadeTest(
            roundingWeight = 0.5,
            currentState = AtomicReference(
                CurrentState(
                    currentMeltInfo = defaultMeltInfoTest(),
                    lastAngles = ModelAngles(
                        angle = 60.0
                    ),
                    lastSlagRate = ModelSlagRate(
                        avgSteelRate = 0.16
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

        runBlocking {
            converterFacade.handleMath(context)
        }

        assertEquals(CorStatus.SUCCESS, context.status)
        assertEquals(SignalerModel.SignalerLevelModel.NO_SIGNAL, context.signaler.level)
        assertEquals(SignalerSoundModel.NONE, context.signaler.sound)
    }

    // #1061 - Успела выполниться Warning (статус Выполнено), содержание металла пришло в норму
    @Test
    fun signalerTestCase14() {
        val repository = UserEventRepositoryInMemory()
        repository.put("211626-1606203458852", MetalRateWarningEvent(
            id = UUID.randomUUID().toString(),
            timeStart = Instant.now().minusMillis(3000L),
            timeFinish = Instant.now().minusMillis(1000L),
            metalRate = 0.11,
            warningPoint = 0.1,
            angleStart = 66.0
        ))
        val converterFacade = converterFacadeTest(
            currentState = AtomicReference(
                CurrentState(
                    currentMeltInfo = defaultMeltInfoTest(),
                    lastAngles = ModelAngles(
                        angle = 60.0
                    ),
                    lastSlagRate = ModelSlagRate(
                        avgSteelRate = 0.11
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

        runBlocking {
            converterFacade.handleMath(context)
        }

        assertEquals(CorStatus.SUCCESS, context.status)
        assertEquals(SignalerModel.SignalerLevelModel.NO_SIGNAL, context.signaler.level)
        assertEquals(SignalerSoundModel.NONE, context.signaler.sound)
    }

    // #1061 - Успела выполниться Critical (статус Не выполнено), содержание металла пришло в норму
    @Test
    fun signalerTestCase15() {
        val repository = UserEventRepositoryInMemory()
        repository.put("211626-1606203458852", MetalRateCriticalEvent(
            id = UUID.randomUUID().toString(),
            timeStart = Instant.now().minusMillis(3000L),
            timeFinish = Instant.now().minusMillis(1000L),
            metalRate = 0.16,
            criticalPoint = 0.15,
            angleStart = 66.0
        ))
        val converterFacade = converterFacadeTest(
            roundingWeight = 0.5,
            currentState = AtomicReference(
                CurrentState(
                    currentMeltInfo = defaultMeltInfoTest(),
                    lastAngles = ModelAngles(
                        angle = 62.0
                    ),
                    lastSlagRate = ModelSlagRate(
                        avgSteelRate = 0.16
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

        runBlocking {
            converterFacade.handleMath(context)
        }

        assertEquals(CorStatus.SUCCESS, context.status)
        assertEquals(SignalerModel.SignalerLevelModel.NO_SIGNAL, context.signaler.level)
        assertEquals(SignalerSoundModel.NONE, context.signaler.sound)
    }

    // #1061 - Успела выполниться Warning (статус Не выполнено), содержание металла пришло в норму
    @Test
    fun signalerTestCase16() {
        val repository = UserEventRepositoryInMemory()
        repository.put("211626-1606203458852", MetalRateWarningEvent(
            id = UUID.randomUUID().toString(),
            timeStart = Instant.now().minusMillis(3000L),
            timeFinish = Instant.now().minusMillis(1000L),
            metalRate = 0.11,
            warningPoint = 0.1,
            angleStart = 66.0
        ))
        val converterFacade = converterFacadeTest(
            currentState = AtomicReference(
                CurrentState(
                    currentMeltInfo = defaultMeltInfoTest(),
                    lastAngles = ModelAngles(
                        angle = 62.0
                    ),
                    lastSlagRate = ModelSlagRate(
                        avgSteelRate = 0.11
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

        runBlocking {
            converterFacade.handleMath(context)
        }

        assertEquals(CorStatus.SUCCESS, context.status)
        assertEquals(SignalerModel.SignalerLevelModel.NO_SIGNAL, context.signaler.level)
        assertEquals(SignalerSoundModel.NONE, context.signaler.sound)
    }
}
