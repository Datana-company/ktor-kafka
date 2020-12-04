package ru.datana.smart.ui.converter.backend


import kotlinx.coroutines.runBlocking
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.events.IBizEvent
import ru.datana.smart.ui.converter.common.events.MetalRateCriticalEvent
import ru.datana.smart.ui.converter.common.models.CurrentState
import ru.datana.smart.ui.converter.common.models.ModelAngles
import ru.datana.smart.ui.converter.common.models.ModelFrame
import ru.datana.smart.ui.converter.common.models.ModelSlagRate
import ru.datana.smart.ui.converter.repository.inmemory.UserEventRepositoryInMemory
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
    @BeforeTest
    fun prepareMetaDataTestBefore() {

    }
    @Test
    fun isEventActive() {
        val repository = UserEventRepositoryInMemory()
//        repository.put(
//            "211626-1606203458852", MetalRateCriticalEvent(
//                id = UUID.randomUUID().toString(),
//                timeStart = Instant.now().minusMillis(1000L),
//                timeFinish = Instant.now().minusMillis(1000L),
//                metalRate = 0.18,
//                criticalPoint = 0.17,
//                angleStart = 66.0,
//                isActive = true
//            )
//        )

        val converterFacade = converterFacadeTest(
            roundingWeight = 0.5,
            currentState = AtomicReference(
                CurrentState(
                    currentMeltInfo = defaultMeltInfoTest(),
                    lastAngles = ModelAngles(
                        angle = 66.0
                    ),
                    lastSlagRate = ModelSlagRate(
                        avgSteelRate = 0.11,
                        steelRate = 0.11

                    )
                )
            ),
            converterRepository = repository
        )
//       val converterFacade = converterFacadeTest()
        val context = converterBeContextTest(
            meltInfo = defaultMeltInfoTest(),
            slagRate = ModelSlagRate(
                slagRate = 0.11,
                steelRate = 0.11
//                avgSteelRate = 0.11
            ),
            frame = ModelFrame(
                frameTime = Instant.now()
            )
        )


        runBlocking {
            converterFacade.handleMath(context)
        }

//        assertEquals(IBizEvent.Category.CRITICAL, context.events.events.first().category)
        assertEquals(false,  context.events.events.first().isActive)


//            context.eventsRepository.getActiveByMeltId("211626-1606203458852")
//            .forEach() { it.isActive })
//        eventsRepository.getActiveByMeltId("211626-1606203458852"). isActive)
    }
}

