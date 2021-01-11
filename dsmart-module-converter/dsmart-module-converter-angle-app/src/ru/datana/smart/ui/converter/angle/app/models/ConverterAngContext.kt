package ru.datana.smart.ui.converter.angle.app.models

import org.apache.kafka.clients.producer.Producer
import ru.datana.smart.ui.converter.common.context.CorError
import ru.datana.smart.ui.converter.common.context.CorStatus
import ru.datana.smart.ui.converter.common.models.ModelFrame
import ru.datana.smart.ui.converter.common.models.ModelMeltInfo
import ru.datana.smart.ui.converter.common.models.ModelSlagRate
import java.time.Instant

data class ConverterAngContext(
    var timeStart: Instant = Instant.MIN,
    var topic: String = "",

    var converterId: String = "",
    var topicAngle: String = "",
    var scheduleBasePath: String = "",
    var schedulePath: String = "",

    var frame: ModelFrame = ModelFrame.NONE,
    var meltInfo: ModelMeltInfo = ModelMeltInfo.NONE,
    var slagRate: ModelSlagRate = ModelSlagRate.NONE,
    var angleMessage: AngleMessage = AngleMessage.NONE,

    var producer: Producer<String, String> = NoneProducer,
    var angleSchedule: AngleSchedule = AngleSchedule.NONE,

    var errors: MutableList<CorError> = mutableListOf(),
    var status: CorStatus = CorStatus.NONE
)
