package ru.datana.smart.ui.converter.common.extensions

import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.models.*
import java.time.Instant
import java.util.concurrent.atomic.AtomicReference
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

internal class EventsCreateExtensionsNKR1062Test {

    lateinit var angles: ModelAngles
    lateinit var slagRate: ModelSlagRate
    lateinit var meltInfo: ModelMeltInfo
    lateinit var currentState: CurrentState
    lateinit var context: ConverterBeContext

    @BeforeTest
    fun contextInit(){
        angles = ModelAngles(angle = 50.0)
        slagRate = ModelSlagRate(steelRate = 0.7, slagRate = 0.3)
        meltInfo = ModelMeltInfo(id = "test-melt-id")
        currentState = CurrentState( avgStreamRate = slagRate.slagRate, lastAngles = angles, currentMeltInfo = meltInfo)
        context = ConverterBeContext(
            meltInfo = meltInfo,
            streamRateWarningPoint = 0.23,
            streamRateCriticalPoint = 0.29,
            currentState = AtomicReference(currentState),
            timeStart = Instant.parse("2020-12-20T18:35:24.010Z")
        )
    }

    @Test
    fun eventSlagWarningReachedCreateTest(){
        val event = context.eventSlagWarningReached()
        //println(event)
        assertEquals("В потоке детектирован шлак – 30% сверх допустимой нормы 23%. Верните конвертер в вертикальное положение.", event.textMessage)
        assertEquals("Предупреждение", event.title)
        assertEquals(ModelEvent.EventType.STREAM_RATE_WARNING_EVENT, event.type)
        assertEquals(50.0, event.angleStart)
        assertEquals("2020-12-20T18:35:24.010Z", event.timeStart.toString())
        assertEquals("test-melt-id", event.meltId)
        assertEquals(ModelEvent.Category.WARNING, event.category)
    }

    @Test
    fun eventMetalWarningReachedCreateTest(){
        val event = context.eventMetalWarningReached()
        //println(event)
        assertEquals("В потоке детектирован металл – 30% сверх допустимой нормы 23%. Верните конвертер в вертикальное положение.", event.textMessage)
        assertEquals("Предупреждение", event.title)
        assertEquals(ModelEvent.EventType.STREAM_RATE_WARNING_EVENT, event.type)
        assertEquals(50.0, event.angleStart)
        assertEquals("2020-12-20T18:35:24.010Z", event.timeStart.toString())
        assertEquals("test-melt-id", event.meltId)
        assertEquals(ModelEvent.Category.WARNING, event.category)
    }

    @Test
    fun eventMetalCriticalReachedCreateTest(){
        val event = context.eventMetalCriticalReached()
        //println(event)
        assertEquals("В потоке детектирован металл – 30%, процент потерь превышает критическое значение – 29%. Верните конвертер в вертикальное положение!", event.textMessage)
        assertEquals("Критическая ситуация", event.title)
        assertEquals(ModelEvent.EventType.STREAM_RATE_CRITICAL_EVENT, event.type)
        assertEquals(50.0, event.angleStart)
        assertEquals("2020-12-20T18:35:24.010Z", event.timeStart.toString())
        assertEquals("test-melt-id", event.meltId)
        assertEquals(ModelEvent.Category.CRITICAL, event.category)
    }

    @Test
    fun eventSlagCriticalReachedCreateTest(){
        val event = context.eventSlagCriticalReached()
        //println(event)
        assertEquals("В потоке детектирован шлак – 30%, процент потерь превышает критическое значение – 29%. Верните конвертер в вертикальное положение!", event.textMessage)
        assertEquals("Критическая ситуация", event.title)
        assertEquals(ModelEvent.EventType.STREAM_RATE_CRITICAL_EVENT, event.type)
        assertEquals(50.0, event.angleStart)
        assertEquals("2020-12-20T18:35:24.010Z", event.timeStart.toString())
        assertEquals("test-melt-id", event.meltId)
        assertEquals(ModelEvent.Category.CRITICAL, event.category)
    }

    @Test
    fun eventSlagInfoReachedCreateTest(){
        val event = context.eventSlagInfoReached()
        //println(event)
        assertEquals("Достигнут предел потерь шлака в потоке – 30%.", event.textMessage)
        assertEquals("Информация", event.title)
        assertEquals(ModelEvent.EventType.STREAM_RATE_INFO_EVENT, event.type)
        assertEquals(50.0, event.angleStart)
        assertEquals("2020-12-20T18:35:24.010Z", event.timeStart.toString())
        assertEquals("test-melt-id", event.meltId)
        assertEquals(ModelEvent.Category.INFO, event.category)
    }

    @Test
    fun eventMetalInfoReachedCreateTest(){
        val event = context.eventMetalInfoReached()
        //println(event)
        assertEquals("Достигнут предел потерь металла в потоке – 30%.", event.textMessage)
        assertEquals("Информация", event.title)
        assertEquals(ModelEvent.EventType.STREAM_RATE_INFO_EVENT, event.type)
        assertEquals(50.0, event.angleStart)
        assertEquals("2020-12-20T18:35:24.010Z", event.timeStart.toString())
        assertEquals("test-melt-id", event.meltId)
        assertEquals(ModelEvent.Category.INFO, event.category)
    }

    @Test
    fun eventMetalSuccessReachedCreateTest(){
        val event = context.eventMetalSuccessReached()
        assertEquals("Информация", event.title)
        assertEquals(ModelEvent.Category.INFO, event.category)
        assertEquals(ModelEvent.EventType.SUCCESS_MELT_EVENT, event.type)
        assertEquals("Допустимая норма потерь металла 23% не была превышена.", event.textMessage)
        assertFalse { event.isActive }
        assertEquals("2020-12-20T18:35:24.010Z", event.timeStart.toString())
        assertEquals("test-melt-id", event.meltId)
    }

    @Test
    fun eventSlagSuccessReachedCreateTest(){
        val event = context.eventSlagSuccessReached()
        assertEquals("Информация", event.title)
        assertEquals(ModelEvent.Category.INFO, event.category)
        assertEquals(ModelEvent.EventType.SUCCESS_MELT_EVENT, event.type)
        assertEquals("Допустимая норма потерь шлака 23% не была превышена.", event.textMessage)
        assertFalse { event.isActive }
        assertEquals("2020-12-20T18:35:24.010Z", event.timeStart.toString())
        assertEquals("test-melt-id", event.meltId)
    }
}
