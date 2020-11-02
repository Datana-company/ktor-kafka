package ru.datana.smart.ui.converter.mock.app

import ru.datana.smart.ui.converter.mock.app.models.UploadDataModel

data class ConverterMockContext(
    var status: Statuses = Statuses.NONE,
    var startCase: String = "",
    var requestToSave: ConverterCaseSaveRequest = ConverterCaseSaveRequest(),
    var responseData: ConverterCaseListModel = ConverterCaseListModel(),
    var responseToSave: ConverterCaseSaveResponse = ConverterCaseSaveResponse(),
    var uploadDataModel: UploadDataModel = UploadDataModel(emptyMap())
) {
    enum class Statuses {
        NONE,
        OK,
        ERROR
    }
}
