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

            +DevicesFilterHandler
            +MeltFilterHandler
            +FrameTimeFilterHandler

            handler {
                onEnv { status == CorStatus.STARTED }
                exec {
                    frame.channel = ModelFrame.Channels.MATH
                }
            }

            +EncodeBase64Handler
            +WsSendMathFrameHandler
            konveyor {
                // Временный фильтр на выбросы матмодели по содержанию металла из-за капель металла
                // в начале и в конце слива
                on {
                    val res = slagRate.steelRate <= 0.35
                    val sr = slagRate
                    val mi = meltInfo
                    if (! res) {
                        logger.debug("Filtering out slagRate due to too high value for steelRate", object {
                            val eventType: String = "dsmart-converter-ui-slagRate-filter-highsteel"
                            val slagRate: ModelSlagRate = sr
                            val meltInfo: ModelMeltInfo = mi
                        })
                    }
                    res
                }

                +CalcAvgSteelRateHandler
                +WsSendMathSlagRateHandler

                // Обновляем информацию о последнем значении slagRate
                handler {
                    on { status == CorStatus.STARTED}
                    exec {
                        val curState = currentState.get()
                        curState.lastSlagRate = slagRate
                    }
                }

                handler {
                    onEnv { status == CorStatus.STARTED }
                    exec {
                        converterFacade.handleEvents(this)
                    }
                }
            }
            +WsSendMeltFinishHandler

            +FinishHandler
        }
    }
}
