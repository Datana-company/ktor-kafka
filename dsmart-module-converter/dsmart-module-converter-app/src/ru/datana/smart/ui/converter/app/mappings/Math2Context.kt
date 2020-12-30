import ru.datana.smart.converter.transport.math.ConverterDeviceTypeOuterClass
import ru.datana.smart.converter.transport.math.ConverterTransportMlUiOuterClass
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.models.*
import java.time.Instant

fun ConverterBeContext.setMeltInfo(converterTransportMlUi: ConverterTransportMlUiOuterClass.ConverterTransportMlUi) {
    this.meltInfo = ModelMeltInfo(
        id = converterTransportMlUi.meltInfo?.id ?: "",
        timeStart = converterTransportMlUi.meltInfo?.timeStart?.let { Instant.ofEpochMilli(it) } ?: Instant.MIN,
        meltNumber = converterTransportMlUi.meltInfo?.meltNumber ?: "",
        steelGrade = converterTransportMlUi.meltInfo?.steelGrade ?: "",
        crewNumber = converterTransportMlUi.meltInfo?.crewNumber ?: "",
        shiftNumber = converterTransportMlUi.meltInfo?.shiftNumber ?: "",
        mode = converterTransportMlUi.meltInfo?.mode?.let {
//            when(it) {
//                ConverterWorkModeOuterClass.ConverterWorkMode.
//            }
            null
        } ?: ModelMeltInfo.Mode.NONE,
        devices = ModelMeltDevices(
            converter = ModelDevicesConverter(
                id = converterTransportMlUi.meltInfo?.devices?.converter?.id ?: "",
                name = converterTransportMlUi.meltInfo?.devices?.converter?.name ?: "",
                uri = converterTransportMlUi.meltInfo?.devices?.converter?.uri ?: "",
                deviceType = converterTransportMlUi.meltInfo?.devices?.converter?.deviceType ?: "",
                type = converterTransportMlUi.meltInfo?.devices?.irCamera?.type.toModel()
            ),
            irCamera = ModelDevicesIrCamera(
                id = converterTransportMlUi.meltInfo?.devices?.irCamera?.id ?: "",
                name = converterTransportMlUi.meltInfo?.devices?.irCamera?.name ?: "",
                uri = converterTransportMlUi.meltInfo?.devices?.irCamera?.uri ?: "",
                deviceType = converterTransportMlUi.meltInfo?.devices?.irCamera?.deviceType ?: "",
                type = converterTransportMlUi.meltInfo?.devices?.irCamera?.type.toModel()
            ),
            selsyn = ModelDevicesSelsyn(
                id = converterTransportMlUi.meltInfo?.devices?.selsyn?.id ?: "",
                name = converterTransportMlUi.meltInfo?.devices?.selsyn?.name ?: "",
                uri = converterTransportMlUi.meltInfo?.devices?.selsyn?.uri ?: "",
                deviceType = converterTransportMlUi.meltInfo?.devices?.selsyn?.deviceType ?: "",
                type = converterTransportMlUi.meltInfo?.devices?.irCamera?.type.toModel()
            ),
            slagRate = ModelDevicesSlagRate(
                id = converterTransportMlUi.meltInfo?.devices?.slagRate?.id ?: "",
                name = converterTransportMlUi.meltInfo?.devices?.slagRate?.name ?: "",
                uri = converterTransportMlUi.meltInfo?.devices?.slagRate?.uri ?: "",
                deviceType = converterTransportMlUi.meltInfo?.devices?.slagRate?.deviceType ?: "",
                type = converterTransportMlUi.meltInfo?.devices?.irCamera?.type.toModel()
            )
        )
    )
}

fun ConverterBeContext.setFrame(converterTransportMlUi: ConverterTransportMlUiOuterClass.ConverterTransportMlUi) {
    // будут браться другие поля, когда они появятся
    this.frame = ModelFrame(
        frameId = converterTransportMlUi.frameId ?: "",
        frameTime = converterTransportMlUi.frameTime.let { Instant.ofEpochMilli(it) } ?: Instant.MIN,
        framePath = converterTransportMlUi.framePath ?: "",
        buffer = converterTransportMlUi.math.frame.toByteArray()
    )
}

fun ConverterBeContext.setSlagRate(converterTransportMlUi: ConverterTransportMlUiOuterClass.ConverterTransportMlUi) {
    this.slagRate = ModelSlagRate(
        steelRate = converterTransportMlUi.math.steelRate,
        slagRate = converterTransportMlUi.math.slagRate,
    )
}

fun ConverterDeviceTypeOuterClass.ConverterDeviceType?.toModel() = let { /*ModelDeviceType.valueOf(it.name)*/ null }
    ?: ModelDeviceType.NONE

fun ConverterBeContext.of(mathMessage: ConverterTransportMlUiOuterClass.ConverterTransportMlUi) = apply {
    setMeltInfo(mathMessage)
    setFrame(mathMessage)
    setSlagRate(mathMessage)
}
