package ru.datana.smart.ui.converter.mock.app

import ru.datana.smart.ui.meta.models.ConverterMeltInfo

data class ConverterMockContext(
    var status: Statuses = Statuses.NONE,
    var startCase: String = "",
    var requestToSave: ConverterCaseSaveRequest = ConverterCaseSaveRequest(),
    var responseData: ConverterCaseListModel = ConverterCaseListModel(),
    var responseToSave: ConverterCaseSaveResponse = ConverterCaseSaveResponse(),
) {
    enum class Statuses {
        NONE,
        OK,
        ERROR
    }
}
