package ru.datana.smart.ui.converter.angle.app.cor

import codes.spectrum.konveyor.IKonveyorEnvironment
import codes.spectrum.konveyor.IKonveyorHandler
import codes.spectrum.konveyor.konveyor
import org.apache.kafka.clients.producer.Producer
import ru.datana.smart.logger.datanaLogger
import ru.datana.smart.ui.converter.angle.app.mappings.jacksonSerializer
import ru.datana.smart.ui.converter.angle.app.models.AngleSchedule
import ru.datana.smart.ui.converter.angle.app.models.ConverterAngContext
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.ModelFrame
import ru.datana.smart.ui.converter.common.models.ModelMeltInfo
import ru.datana.smart.ui.converter.common.models.ModelSlagRate
import java.io.File
import java.time.Duration
import java.time.Instant

class ConverterAngLogics(
    val converterId: String,
    val producer: Producer<String, String>,
    val angleSchedule: AngleSchedule,
    val topicAngle: String,
    val scheduleBasePath: String
) : IKonveyorHandler<ConverterAngContext> {

    override suspend fun exec(context: ConverterAngContext, env: IKonveyorEnvironment) {
        with(context) {
            converterId = this@ConverterAngLogics.converterId
            producer = this@ConverterAngLogics.producer
            angleSchedule = this@ConverterAngLogics.angleSchedule
            topicAngle = this@ConverterAngLogics.topicAngle
            scheduleBasePath = this@ConverterAngLogics.scheduleBasePath
        }
        println("Context: $context")
        chain.exec(context)
    }

    override fun match(context: ConverterAngContext, env: IKonveyorEnvironment): Boolean = true

    companion object {
        val logger = datanaLogger(this::class.java)
        val chain = konveyor<ConverterAngContext> {
            exec { status = CorStatus.STARTED } // Выставляем статус старта
            exec { logger.debug("Starting chain") }

            // Фильтр по конвертеру
            handler {
                on { converterId != meltInfo.devices.converter.id }
                exec {
                    status = CorStatus.FINISHED
                    logger.debug(
                        "Filter out object due to converterId is wrong: " +
                            "expecting $converterId, while actual is ${meltInfo.devices.converter.id}"
                    )
                }
            }

            // Цепочка для обработки сообщений от матмодели
            konveyor {
                on { frame != ModelFrame.NONE && status == CorStatus.STARTED }

                // Фильр по наличию данных в сообщении
                handler {
                    on { angleSchedule.items.isEmpty() || frame.frameTime == Instant.MIN || meltInfo.timeStart == Instant.MIN }
                    exec {
                        status = CorStatus.FINISHED
                        logger.error(
                            "finishing chaing due to wrong data : ${angleSchedule.items}, ${frame.frameTime}, ${meltInfo.timeStart}"
                        )
                    }
                }
                // Ищем ближайшее по времени сообщение
                handler {
                    on { status == CorStatus.STARTED }
                    exec {
                        logger.debug("scheduling response")
                        val timeShift = Duration.between(meltInfo.timeStart, frame.frameTime)
                        logger.debug("scheduling response with timeShift: $timeShift")
                        val closestMessage = angleSchedule.getClosest(timeShift.toMillis())
                        logger.debug("scheduling response with closestMessage: $closestMessage")
                        if (closestMessage != null) {
                            angleMessage = closestMessage
                            logger.debug("scheduling response with angleMessage: $angleMessage")
                        }
                    }
                }
                exec { status = CorStatus.FINISHED } // Завершаем обработку
            }

            // Цепочка для обработки сообщений с мета-информацией
            konveyor {
                on { meltInfo != ModelMeltInfo.NONE && status == CorStatus.STARTED }

                exec { angleSchedule.clean() } // Очищаем старое расписание
                exec { // Вычисляем путь до файла с расписанием
                    val scheduleRelativePath = meltInfo.devices.selsyn.uri
                    schedulePath = "${scheduleBasePath}/${scheduleRelativePath}"
                }
                exec { // Чтение расписания углов из файла
                    val schedule = jacksonSerializer.readValue(
                        File(schedulePath),
                        AngleSchedule::class.java
                    )
                    angleSchedule.addAll(schedule.items)
                    logger.debug("Got schedule with ${angleSchedule.items.size} elements")
                }
                exec { status = CorStatus.FINISHED } // Завершаем обработку
            }
        }
    }
}
