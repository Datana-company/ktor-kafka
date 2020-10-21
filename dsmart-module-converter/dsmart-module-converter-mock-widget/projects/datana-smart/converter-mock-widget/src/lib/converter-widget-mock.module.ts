import {NgModule} from '@angular/core';
import {ConverterWidgetMockComponent} from './converter-widget-mock.component';
import {MockListComponent} from "./mock-list/mock-list.component";
import {MockListItemComponent} from "./mock-list-item/mock-list-item.component";
import {CommonModule} from "@angular/common";
import {HttpClientModule} from "@angular/common/http";
import {FileUploadComponent} from "./file-upload/file-upload.component";
import {CaseEditorComponent} from "./case-editor/case-editor.component";

@NgModule({
    declarations: [
        MockListComponent,
        MockListItemComponent,
        ConverterWidgetMockComponent,
        FileUploadComponent,
        CaseEditorComponent
    ],
    imports: [
        CommonModule,
        HttpClientModule
    ],
    exports: [ConverterWidgetMockComponent]
})
export class ConverterWidgetMockModule {
}
