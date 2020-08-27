package ru.datana.smart.ui.transport.models.kmp

abstract class KmpDsmartUiResponse<T>(
  val status: KmpDsmartUiResponseStatuses
) {
  abstract val data: T

}
