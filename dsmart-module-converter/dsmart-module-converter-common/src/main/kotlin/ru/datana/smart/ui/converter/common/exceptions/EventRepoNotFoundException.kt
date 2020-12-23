package ru.datana.smart.ui.converter.common.exceptions

class EventRepoNotFoundException(id: String) : RuntimeException("Object with ID=$id is not found")
