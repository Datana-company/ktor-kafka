package ru.datana.smart.ui.converter.backend


import ru.datana.smart.ui.converter.common.events.MetalRateCriticalEvent
import ru.datana.smart.ui.converter.repository.inmemory.UserEventRepositoryInMemory
import java.time.Instant
import kotlin.test.Test
import java.util.*

internal class EventsChainTest1 {
//    NKR-1031
    @Test
    fun isEventActive ()  {
        val repository = UserEventRepositoryInMemory()
        repository.put("211626-1606203458852", MetalRateCriticalEvent(
            id = UUID.randomUUID().toString(),
            timeStart = Instant.now().minusMillis(1000L),
            timeFinish = Instant.now().minusMillis(1000L),
            metalRate = 0.18,
            criticalPoint = 0.17,
            angleStart = 66.0
        )
        )
    }

}

