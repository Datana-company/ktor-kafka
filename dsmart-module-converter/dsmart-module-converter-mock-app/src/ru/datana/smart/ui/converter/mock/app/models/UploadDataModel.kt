package ru.datana.smart.ui.converter.mock.app.models

import io.ktor.http.content.PartData

class UploadDataModel(map: Map<String?, PartData>) {
    val file: PartData.FileItem by map
    val fileName: PartData.FormItem by map
    val newCaseFolderName: PartData.FormItem by map

    override fun toString() = "Source file name: ${file.originalFileName}," +
        " file new name: ${fileName.value}," +
        " new case folder name: ${newCaseFolderName.value}"
}
