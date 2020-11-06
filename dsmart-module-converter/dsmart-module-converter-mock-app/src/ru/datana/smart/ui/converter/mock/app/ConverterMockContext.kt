package ru.datana.smart.ui.converter.mock.app

import ru.datana.smart.ui.converter.mock.app.models.ConverterCaseListModel
import ru.datana.smart.ui.converter.mock.app.models.ConverterCaseSaveRequest
import ru.datana.smart.ui.converter.mock.app.models.ConverterCaseSaveResponse
import ru.datana.smart.ui.converter.mock.app.models.UploadDataModel
import ru.datana.smart.ui.meta.models.ConverterMeltInfo
import java.io.File

data class ConverterMockContext(
    var status: Statuses = Statuses.NONE,
    var startCase: String = "",
    var requestToSave: ConverterCaseSaveRequest = ConverterCaseSaveRequest(),
    var responseData: ConverterCaseListModel = ConverterCaseListModel(),
    var responseToSave: ConverterCaseSaveResponse = ConverterCaseSaveResponse(),
    var caseId: String = "",
    var caseDir: File = EMPTY_FILE,
    var fileVideoSaved: File = EMPTY_FILE,
    var selsynJsonSaved: File = EMPTY_FILE,
    var slagRateJsonSaved: File = EMPTY_FILE,
    var meltInfo: ConverterMeltInfo = ConverterMeltInfo(),
    val errors: MutableList<Throwable> = mutableListOf()
) {

    enum class Statuses {
        NONE,
        OK,
        ERROR
    }

    companion object {
        val EMPTY_FILE = File("")
    }
}
