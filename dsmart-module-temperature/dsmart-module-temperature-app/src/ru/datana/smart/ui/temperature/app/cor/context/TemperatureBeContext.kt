package ru.datana.smart.ui.temperature.app.cor.context

import kotlinx.serialization.json.Json
import ru.datana.smart.logger.DatanaLogContext

class TemperatureBeContext<K, V> (
    val records: Collection<Record<K, V>>,
    val logger: DatanaLogContext,
    val jacksonMapper: Any,
    val kotlinxJson: Json
) {}
