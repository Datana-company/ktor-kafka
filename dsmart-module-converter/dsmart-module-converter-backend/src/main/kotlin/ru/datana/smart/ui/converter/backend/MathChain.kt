package ru.datana.smart.ui.converter.backend

import codes.spectrum.konveyor.DefaultKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.konveyor
import ru.datana.smart.ui.converter.backend.common.ConverterChainSettings
import ru.datana.smart.ui.converter.backend.common.setSettings
import ru.datana.smart.logger.datanaLogger
import ru.datana.smart.ui.converter.backend.handlers.*
import ru.datana.smart.ui.converter.common.context.ConverterBeContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.ModelEventMode
import ru.datana.smart.ui.converter.common.models.ModelFrame

/*
* MathChain - цепочка обработки данных из матмодели (кадры и содержание потока).
* */
class MathChain(
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

        val logger = datanaLogger(this::class.java)
        val konveyor = konveyor<ConverterBeContext> {

            +DevicesFilterHandler // фильтр данных по идетификатору устройства
            +MeltFilterHandler // фильтр данных по идентификатору плавки
            +FrameTimeFilterHandler // фильтр данных по времени кадра из матмодели

            // задаётся канал (источник) кадра
            handler {
                onEnv { status == CorStatus.STARTED }
                exec {
                    frame.channel = ModelFrame.Channels.MATH
                }
            }

            +EncodeBase64Handler // кодирование кадра в base64

            +WsSendMathFrameHandler // отправка кадра по web-socket и отправка пустых данных
//            konveyor {
                // Временный фильтр на выбросы матмодели по содержанию металла из-за капель металла
                // в начале и в конце слива
//                on {
//                    val res = slagRate.steelRate <= 0.35
//                    val sr = slagRate
//                    val mi = meltInfo
//                    if (! res) {
//                        logger.debug("Filtering out slagRate due to too high value for steelRate", object {
//                            val eventType: String = "dsmart-converter-ui-slagRate-filter-highsteel"
//                            val slagRate: ModelSlagRate = sr
//                            val meltInfo: ModelMeltInfo = mi
//                        })
//                    }
//                    res
//                }

                +CalcAvgRateHandler // вычисление усреднённого значения

                // обновление информации о последнем значении содержания потока,
                // а затем достаются все данные по содержанию потока, касающиеся текущей плавки
                handler {
                    on { status == CorStatus.STARTED }
                    exec {
                        currentStateRepository.addSlagRate(converterId, slagRate)
                        slagRateList = currentStateRepository.getAllSlagRates(converterId)?: mutableListOf()
                    }
                }

                +WsSendMathSlagRatesHandler // отправка данных о содержании потока по web-socket и отправка пустых данных

                // вызов цепочки обработки событий по металлу
                handler {
                    onEnv { status == CorStatus.STARTED && eventMode == ModelEventMode.STEEL }
                    exec {
                        converterFacade.handleSteelEvents(this)
                    }
                }

                // вызов цепочки обработки событий по шлаку
                handler {
                    onEnv { status == CorStatus.STARTED && eventMode == ModelEventMode.SLAG }
                    exec {
                        converterFacade.handleSlagEvents(this)
                    }
                }

                // вызов цепочки обработки светофора
                handler {
                    onEnv { status == CorStatus.STARTED }
                    exec {
                        converterFacade.handleSignaler(this)
                    }
                }

                // отправка данных о статусе потока на фронтенд по web-socket
                handler {
                    onEnv { status == CorStatus.STARTED }
                    exec {
                        wsManager.sendStreamStatus(this)
                    }
                }
//            }

            +WsSendMeltFinishHandler //определение конца плавки и отправки завершающих значений на фронт

            +FinishHandler // обработчик завершения цепочки
        }
    }
}
