package ru.datana.smart.ui.converter.backend

import codes.spectrum.konveyor.DefaultKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.konveyor
import ru.datana.smart.ui.converter.backend.common.ConverterChainSettings
import ru.datana.smart.ui.converter.backend.common.setSettings
import ru.datana.smart.ui.converter.backend.handlers.*
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.*

/*
* FrameChain - цепочка обработки кадров из видеоадаптера.
* */
class FrameChain(
    var chainSettings: ConverterChainSettings
) {

    suspend fun exec(context: ConverterBeContext) {
        exec(context, DefaultKonveyorEnvironment)
    }

    suspend fun exec(context: ConverterBeContext, env: IKonveyorEnvironment) {
        context.setSettings(chainSettings)
        konveyor.exec(context, env)
    }

    companion object {
        val konveyor = konveyor<ConverterBeContext> {

            +DevicesFilterHandler // фильтр данных по идетификатору устройства
            +MeltFilterHandler // фильтр данных по идентификатору плавки
            +FrameTimeFilterHandler // фильтр данных по времени кадра из видеоадаптера

            // задаётся канал (источник) кадра
            handler {
                onEnv { status == CorStatus.STARTED }
                exec {
                    frame.channel = ModelFrame.Channels.CAMERA
                }
            }

            +EncodeBase64Handler // кодирование кадра в base64

            +WsSendFrameHandler // отправка кадра по web-socket и отправка пустых данных
            +WsSendMeltFinishHandler // определение конца плавки и отправки завершающих значений на фронт

            +FinishHandler // обработчик завершения цепочки
        }
    }
}
