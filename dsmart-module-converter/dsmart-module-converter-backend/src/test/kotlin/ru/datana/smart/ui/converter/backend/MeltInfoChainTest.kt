package ru.datana.smart.ui.converter.backend

import kotlinx.coroutines.runBlocking
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.CurrentState
import ru.datana.smart.ui.converter.repository.inmemory.currentstate.CurrentStateRepositoryInMemory
import kotlin.test.*
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

internal class MeltInfoChainTest {

    lateinit var converterFacade: ConverterFacade
    lateinit var context: ConverterBeContext

    @OptIn(ExperimentalTime::class)
    @BeforeTest
    fun metaTestBefore() {
        runBlocking {
            val meltInfo = meltInfoTest(
                meltId = "211626-1606203458852",
                converterId = "converter2",
                irCameraName = "IR camera for Converter",
                irCameraId = "ir-cam-25",
            )
            converterFacade = converterFacadeTest()
            context = converterBeContextTest(
                meltInfo = meltInfo,
                currentStateRepository = CurrentStateRepositoryInMemory(30.toDuration(DurationUnit.SECONDS)).apply {
                    create(CurrentState(currentMeltInfo = meltInfo))
                }
            ) }
    }

    @Test
    fun metaTest() {
        runBlocking {
            converterFacade.handleMeltInfo(context)

            assertEquals(CorStatus.SUCCESS, context.status)
            assertNotEquals(context.meltInfo.id, context.currentStateRepository.currentMeltId(context.converterId))
        }

    }

    @Test
    fun irCameraNameAndIdTestNKR1071AndNKR954() {
        runBlocking {
            converterFacade.handleMeltInfo(context)
        }

        assertEquals(CorStatus.SUCCESS, context.status)
        assertEquals("IR camera for Converter", context.meltInfo.devices.irCamera.name)
        assertEquals("ir-cam-25", context.meltInfo.devices.irCamera.id)
        assertFalse( context.meltInfo.devices.irCamera.name == "IR camera for Converter-test",)
        assertFalse("ir-cam-25-test" == context.meltInfo.devices.irCamera.id)
    }
}

