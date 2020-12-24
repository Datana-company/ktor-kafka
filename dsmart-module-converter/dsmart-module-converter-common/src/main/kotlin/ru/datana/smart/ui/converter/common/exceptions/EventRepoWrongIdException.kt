package ru.datana.smart.ui.converter.common.exceptions

class EventRepoWrongIdException(id: String) : Throwable("Wrong ID in operation: $id")
