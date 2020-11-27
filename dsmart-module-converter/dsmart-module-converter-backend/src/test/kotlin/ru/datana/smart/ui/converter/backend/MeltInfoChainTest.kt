package ru.datana.smart.ui.converter.backend

import kotlinx.coroutines.runBlocking
import ru.datana.smart.ui.converter.common.context.CorStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

internal class MeltInfoChainTest {

    @Test
    fun metaTest() {
        val converterFacade = converterFacadeTest()
        val context = converterBeContextTestCase(
            params = TestCaseParams(
                meltId = "211626-1606203458852",
                converterId = "converter2"
            )
        )

        runBlocking {
            converterFacade.handleMeltInfo(context)
        }

        assertEquals(CorStatus.SUCCESS, context.status)
        assertNotEquals(context.meltInfo.id, context.currentState.get().currentMeltInfo.id)
    }
}

