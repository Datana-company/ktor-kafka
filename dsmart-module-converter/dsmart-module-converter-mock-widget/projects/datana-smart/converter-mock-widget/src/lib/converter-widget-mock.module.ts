import { NgModule } from '@angular/core';
import { ConverterWidgetMockComponent } from './converter-widget-mock.component';
import {MockListComponent} from "./mock-list/mock-list.component";
import {MockListItemComponent} from "./mock-list-item/mock-list-item.component";

@NgModule({
  declarations: [
      MockListComponent,
      MockListItemComponent,
      ConverterWidgetMockComponent
  ],
  imports: [
  ],
  exports: [ConverterWidgetMockComponent]
})
export class ConverterWidgetMockModule { }
