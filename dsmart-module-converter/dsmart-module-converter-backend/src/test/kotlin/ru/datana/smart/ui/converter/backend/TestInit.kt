package ru.datana.smart.ui.converter.backend

import ru.datana.smart.ui.converter.app.websocket.WsManager
import ru.datana.smart.ui.converter.app.websocket.WsSignalerManager
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.models.ModelEventMode
import ru.datana.smart.ui.converter.common.models.*
import ru.datana.smart.ui.converter.common.repositories.IEventRepository
import ru.datana.smart.ui.converter.repository.inmemory.EventRepositoryInMemory
import java.time.Instant
import java.util.*
import java.util.concurrent.atomic.AtomicReference
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

@OptIn(ExperimentalTime::class)
fun converterFacadeTest(
    converterRepository: IEventRepository? = null,
    wsManager: IWsManager? = null,
    wsSignalerManager: IWsSignalerManager? = null,
    dataTimeout: Long? = null,
    meltTimeout: Long? = null,
    eventMode: ModelEventMode? = null,
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
        converterRepository = converterRepository ?: EventRepositoryInMemory(ttl = 10.toDuration(DurationUnit.MINUTES)),
        wsManager = wsManager ?: WsManager(),
        wsSignalerManager = wsSignalerManager ?: WsSignalerManager(),
        dataTimeout = dataTimeout ?: 3000L,
        meltTimeout = meltTimeout ?: 10000L,
        eventMode = eventMode ?: ModelEventMode.STEEL,
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
    timeStart: Instant? = null,
    meltInfo: ModelMeltInfo? = null,
    angles: ModelAngles? = null,
    frame: ModelFrame? = null,
    slagRate: ModelSlagRate? = null
) =
    ConverterBeContext(
        timeStart = timeStart ?: Instant.now(),
        reactionTime = Long.MIN_VALUE,
        meltInfo = meltInfo ?: defaultMeltInfoTest(),
        angles = angles ?: ModelAngles.NONE,
        frame = frame ?: ModelFrame.NONE,
        slagRate = slagRate ?: ModelSlagRate.NONE
    )

fun createCurrentStateForTest(
    lastAngleTime: Instant? = null,
    lastAngle: Double? = null,
    lastSource: Double? = null,
    lastSteelRate: Double? = null,
    lastSlagRate: Double? = null,
    avgSlagRate: Double? = null,
    avgSteelRate: Double? = null
)
    : AtomicReference<CurrentState> {
    val currentState = AtomicReference(
        CurrentState(
            currentMeltInfo = defaultMeltInfoTest(),
            lastAngles = ModelAngles(
                angleTime = lastAngleTime ?: Instant.MIN,
                angle = lastAngle ?: Double.MIN_VALUE,
                source = lastSource ?: Double.MIN_VALUE
            ),

            lastSlagRate = ModelSlagRate(
                steelRate = lastSteelRate ?: Double.MIN_VALUE,
                slagRate = lastSlagRate ?: Double.MIN_VALUE
            ),
            avgSlagRate = ModelSlagRate(
                steelRate = avgSteelRate ?: Double.MIN_VALUE,
                slagRate = avgSlagRate ?: Double.MIN_VALUE
            )
        )
    )
    return currentState
}

suspend fun createRepositoryWithEventForTest(
    eventType: ModelEvent.EventType,
    timeStart: Instant,
    metalRate: Double? = null,
    criticalPoint: Double? = null,
    warningPoint: Double? = null,
    angleStart: Double? = null,
    category: ModelEvent.Category
)
    : EventRepositoryInMemory {
    val repositoryInMemory = EventRepositoryInMemory()
    repositoryInMemory.create(
        ModelEvent(
            id = UUID.randomUUID().toString(),
            meltId = "211626-1606203458852",
            type = eventType,
            timeStart = timeStart,
            timeFinish = Instant.now().minusMillis(1000L),
            metalRate = metalRate ?: 0.16,
            criticalPoint = criticalPoint ?: 0.15,
            warningPoint = warningPoint ?: 0.1,
            angleStart = angleStart ?: 0.60,
            category = category
        )
    )
    return repositoryInMemory
}


fun defaultMeltInfoTest() =
    meltInfoTest("211626-1606203458852", "converter1")


fun meltInfoTest(
    meltId: String? = null,
    converterId: String? = null,
    irCameraName: String? = null,
    irCameraId: String? = null
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
                id = irCameraId ?: "ir-cam-25",
                name = irCameraName ?: "IR camera for Converter",
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
