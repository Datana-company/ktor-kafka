package ru.datana.smart.ui.converter.common.exceptions

class CurrentStateRepoWrongIdException(id: String): Throwable("Wrong ID in operation: $id")
