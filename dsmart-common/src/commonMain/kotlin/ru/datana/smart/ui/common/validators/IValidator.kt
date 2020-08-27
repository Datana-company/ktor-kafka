package ru.datana.smart.ui.common.validators

interface IValidator<T> {
  fun validate(data: T)
}
