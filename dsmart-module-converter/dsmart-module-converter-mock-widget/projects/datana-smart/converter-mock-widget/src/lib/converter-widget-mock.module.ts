import {InjectionToken, ModuleWithProviders, NgModule} from '@angular/core';
import {ConverterWidgetMockComponent} from './converter-widget-mock.component';
import {MockListComponent} from "./mock-list/mock-list.component";
import {MockListItemComponent} from "./mock-list-item/mock-list-item.component";
import {CommonModule} from "@angular/common";
import {HttpClientModule} from "@angular/common/http";
import {CaseEditorComponent} from "./case-editor/case-editor.component";
import {ReactiveFormsModule} from '@angular/forms';

@NgModule({
    declarations: [
        MockListComponent,
        MockListItemComponent,
        ConverterWidgetMockComponent,
        CaseEditorComponent
    ],
    imports: [
        CommonModule,
        HttpClientModule,
        ReactiveFormsModule
    ],
    exports: [ConverterWidgetMockComponent]
})
export class ConverterWidgetMockModule {
  public static config(hostConfig: HostConfig): ModuleWithProviders<ConverterWidgetMockModule> {
    return {
      ngModule: ConverterWidgetMockModule,
      providers: [{provide: HOST_CONFIG, useValue: hostConfig}]
    };
  }
}

export const HOST_CONFIG: InjectionToken<string> = new InjectionToken('host.config');

export interface HostConfig {
  baseUrl: string;
}

