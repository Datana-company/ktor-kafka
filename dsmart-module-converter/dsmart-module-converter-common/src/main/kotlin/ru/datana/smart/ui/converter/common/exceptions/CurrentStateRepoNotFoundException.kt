package ru.datana.smart.ui.converter.common.exceptions

class CurrentStateRepoNotFoundException(id: String): RuntimeException("Object with ID=$id is not found")
