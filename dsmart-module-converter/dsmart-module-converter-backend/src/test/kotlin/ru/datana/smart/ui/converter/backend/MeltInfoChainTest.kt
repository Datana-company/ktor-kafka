package ru.datana.smart.ui.converter.backend

import kotlinx.coroutines.runBlocking
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

internal class MeltInfoChainTest {

    lateinit var converterFacade: ConverterFacade
    lateinit var context: ConverterBeContext

    @BeforeTest
    fun metaTestBefore() {
        converterFacade = converterFacadeTest()
        context = converterBeContextTest(
            meltInfo = meltInfoTest(
                meltId = "211626-1606203458852",
                converterId = "converter2"
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
}

