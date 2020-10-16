import { NgModule } from '@angular/core';
import { ConverterWidgetMockComponent } from './converter-widget-mock.component';
import {MockListModule} from "@datana-smart/converter-mock-list-component";

@NgModule({
  declarations: [ConverterWidgetMockComponent],
  imports: [
      MockListModule
  ],
  exports: [ConverterWidgetMockComponent]
})
export class ConverterWidgetMockModule { }
