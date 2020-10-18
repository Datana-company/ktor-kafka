import { NgModule } from '@angular/core';
import { ConverterWidgetMockComponent } from './converter-widget-mock.component';
import {MockListComponent} from "./mock-list/mock-list.component";
import {MockListItemComponent} from "./mock-list-item/mock-list-item.component";
import {CommonModule} from "@angular/common";

@NgModule({
  declarations: [
      MockListComponent,
      MockListItemComponent,
      ConverterWidgetMockComponent
  ],
  imports: [
      CommonModule
  ],
  exports: [ConverterWidgetMockComponent]
})
export class ConverterWidgetMockModule { }
