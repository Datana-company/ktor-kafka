package ru.datana.smart.ui.converter.backend

import kotlinx.coroutines.runBlocking
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import kotlin.test.*

internal class MeltInfoChainTest {

    lateinit var converterFacade: ConverterFacade
    lateinit var context: ConverterBeContext

    @BeforeTest
    fun metaTestBefore() {
        converterFacade = converterFacadeTest()
        context = converterBeContextTest(
            meltInfo = meltInfoTest(
                meltId = "211626-1606203458852",
                converterId = "converter2",
                irCameraName = "IR camera for Converter",
                irCameraId = "ir-cam-25",
            )
        )
    }

    @Test
    fun metaTest() {
        runBlocking {
            converterFacade.handleMeltInfo(context)
        }

        assertEquals(CorStatus.SUCCESS, context.status)
        assertNotEquals(context.meltInfo.id, context.currentState.get().currentMeltInfo.id)
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

