package ru.datana.smart.ui.converter.backend

import kotlinx.coroutines.runBlocking
import ru.datana.smart.ui.converter.app.websocket.WsManager
import ru.datana.smart.ui.converter.app.websocket.WsSignalerManager
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.*
import ru.datana.smart.ui.converter.repository.inmemory.UserEventRepositoryInMemory
import java.time.Instant
import java.util.concurrent.atomic.AtomicReference
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

internal class MeltInfoChainTest {

    private val wsManager = WsManager()
    private val wsSignalerManager = WsSignalerManager()

    @Test
    fun metaTest() {
        val converterFacade = ConverterFacade(
            converterRepository = UserEventRepositoryInMemory(),
            wsManager = wsManager,
            wsSignalerManager = wsSignalerManager,
            dataTimeout = 3000L,
            metalRateCriticalPoint = 0.15,
            metalRateWarningPoint = 0.1,
            reactionTime = 3000L,
            sirenLimitTime = 10000L,
            roundingWeight = 0.1,
            currentState = AtomicReference(CurrentState.NONE),
            converterId = "converter1",
            framesBasePath = "123",
            scheduleCleaner = AtomicReference(ScheduleCleaner.NONE)
        )

        val context = ConverterBeContext(
            meltInfo = ModelMeltInfo(
                id = "211626-1606203458852",
                timeStart = Instant.now(),
                meltNumber = "3",
                steelGrade = "X65ME",
                crewNumber = "1",
                shiftNumber = "1",
                mode = ModelMeltInfo.Mode.EMULATION,
                devices = ModelMeltDevices(
                    converter = ModelDevicesConverter(
                        id = "converter2",
                        name = "Converter emulation",
                        uri = "",
                        deviceType = "ConverterDevicesConverter",
                        type = ModelDeviceType.FILE
                    ),
                    irCamera = ModelDevicesIrCamera(
                        id = "ir-cam-25",
                        name = "IR camera for Converter",
                        uri = "case-demo/5.mp4",
                        deviceType = "ConverterDevicesIrCamera",
                        type = ModelDeviceType.FILE
                    ),
                    selsyn = ModelDevicesSelsyn(
                        id = "conv1-selsyn1",
                        name = "Angles mesurement",
                        uri = "case-demo/selsyn.json",
                        deviceType = "ConverterDevicesSelsyn",
                        type = ModelDeviceType.FILE
                    ),
                    slagRate = ModelDevicesSlagRate(
                        id = "conv1-slagRate1",
                        name = "Slag and steel rates resolution",
                        uri = "case-demo/slag-rate.json",
                        deviceType = "ConverterDevicesSlagRate",
                        type = ModelDeviceType.FILE
                    )
                )
            )
        )

        runBlocking {
            converterFacade.handleMeltInfo(context)
        }

        assertEquals(CorStatus.SUCCESS, context.status)
        assertNotEquals(context.meltInfo.id, context.currentState.get().currentMeltInfo.id)
    }
}

