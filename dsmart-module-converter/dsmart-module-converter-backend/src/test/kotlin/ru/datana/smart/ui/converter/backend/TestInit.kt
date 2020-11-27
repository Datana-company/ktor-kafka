package ru.datana.smart.ui.converter.backend

import ru.datana.smart.ui.converter.app.websocket.WsManager
import ru.datana.smart.ui.converter.app.websocket.WsSignalerManager
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.models.*
import ru.datana.smart.ui.converter.repository.inmemory.UserEventRepositoryInMemory
import java.time.Instant
import java.util.concurrent.atomic.AtomicReference

fun converterFacadeTest() =
    ConverterFacade(
        converterRepository = UserEventRepositoryInMemory(),
        wsManager = WsManager(),
        wsSignalerManager = WsSignalerManager(),
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

fun converterBeContextTestCase(params: TestCaseParams) =
    ConverterBeContext(
        meltInfo = ModelMeltInfo(
            id = params.meltId ?: "",
            timeStart = Instant.ofEpochMilli(1606203458852L),
            meltNumber = "3",
            steelGrade = "X65ME",
            crewNumber = "1",
            shiftNumber = "1",
            mode = ModelMeltInfo.Mode.EMULATION,
            devices = ModelMeltDevices(
                converter = ModelDevicesConverter(
                    id = params.converterId ?: "",
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
        ),
        angles = ModelAngles(
            angleTime = params.angleTime ?: Instant.MIN,
            angle = params.angle ?: Double.MIN_VALUE,
            source = params.source ?: Double.MIN_VALUE
        ),
        frame = ModelFrame(
            frameId = params.frameId ?: "",
            frameTime = params.frameTime ?: Instant.MIN,
            framePath = params.framePath ?: "",
            image = params.image ?: "",
            channel = params.channel ?: ModelFrame.Channels.NONE
        ),
        slagRate = ModelSlagRate(
            slagRate = params.slagRate ?: Double.MIN_VALUE,
            steelRate = params.steelRate ?: Double.MIN_VALUE,
            avgSteelRate = params.avgSteelRate ?: Double.MIN_VALUE
        )
    )
