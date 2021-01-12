package ru.datana.smart.ui.converter.common.exceptions

class ConverterDeserializationException(message: String?, cause: Throwable?, source: Any? = null) : RuntimeException(
    source?.ensureIsString()?.let { "$message\nSource string: <<$source>>" } ?: message,
    cause
)

private fun Any.ensureIsString(): String? = try {
    this.toString()
} catch (e: Throwable) {
    null
}
