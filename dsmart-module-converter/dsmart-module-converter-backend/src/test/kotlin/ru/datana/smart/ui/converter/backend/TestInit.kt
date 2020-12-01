package ru.datana.smart.ui.converter.backend

import ru.datana.smart.ui.converter.app.websocket.WsManager
import ru.datana.smart.ui.converter.app.websocket.WsSignalerManager
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.models.*
import ru.datana.smart.ui.converter.common.repositories.IEventRepository
import ru.datana.smart.ui.converter.repository.inmemory.EventRepositoryInMemory
import java.time.Instant
import java.util.concurrent.atomic.AtomicReference

fun converterFacadeTest(
    converterRepository: IEventRepository? = null,
    wsManager: IWsManager? = null,
    wsSignalerManager: IWsSignalerManager? = null,
    dataTimeout: Long? = null,
    metalRateCriticalPoint: Double? = null,
    metalRateWarningPoint: Double? = null,
    reactionTime: Long? = null,
    sirenLimitTime: Long? = null,
    roundingWeight: Double? = null,
    currentState: AtomicReference<CurrentState>? = null,
    converterId: String? = null,
    framesBasePath: String? = null,
    scheduleCleaner: AtomicReference<ScheduleCleaner>? = null,
) =
    ConverterFacade(
        converterRepository = converterRepository ?: EventRepositoryInMemory(),
        wsManager = wsManager ?: WsManager(),
        wsSignalerManager = wsSignalerManager ?: WsSignalerManager(),
        dataTimeout = dataTimeout ?: 3000L,
        metalRateCriticalPoint = metalRateCriticalPoint ?: 0.15,
        metalRateWarningPoint = metalRateWarningPoint ?: 0.1,
        reactionTime = reactionTime ?: 3000L,
        sirenLimitTime = sirenLimitTime ?: 10000L,
        roundingWeight = roundingWeight ?: 0.1,
        currentState = currentState ?: AtomicReference(CurrentState.NONE),
        converterId = converterId ?: "converter1",
        framesBasePath = framesBasePath ?: "123",
        scheduleCleaner = scheduleCleaner ?: AtomicReference(ScheduleCleaner.NONE)
    )

fun converterBeContextTest(
    meltInfo: ModelMeltInfo? = null,
    angles: ModelAngles? = null,
    frame: ModelFrame? = null,
    slagRate: ModelSlagRate? = null
) =
    ConverterBeContext(
        meltInfo = meltInfo ?: defaultMeltInfoTest(),
        angles = angles ?: ModelAngles.NONE,
        frame = frame ?: ModelFrame.NONE,
        slagRate = slagRate ?: ModelSlagRate.NONE
    )

fun defaultMeltInfoTest() =
    meltInfoTest("211626-1606203458852", "converter1")


fun meltInfoTest(
    meltId: String? = null,
    converterId: String? = null
) =
    ModelMeltInfo(
        id = meltId ?: "",
        timeStart = Instant.ofEpochMilli(1606203458852L),
        meltNumber = "3",
        steelGrade = "X65ME",
        crewNumber = "1",
        shiftNumber = "1",
        mode = ModelMeltInfo.Mode.EMULATION,
        devices = ModelMeltDevices(
            converter = ModelDevicesConverter(
                id = converterId ?: "",
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
