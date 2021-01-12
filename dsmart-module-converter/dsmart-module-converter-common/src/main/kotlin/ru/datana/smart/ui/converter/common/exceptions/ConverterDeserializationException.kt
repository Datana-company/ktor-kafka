package ru.datana.smart.ui.converter.common.exceptions

class ConverterDeserializationException(message: String?, cause: Throwable?, source: String? = null)
    : RuntimeException(source?.let { "$message\nSource string: $source" } ?: message, cause)
