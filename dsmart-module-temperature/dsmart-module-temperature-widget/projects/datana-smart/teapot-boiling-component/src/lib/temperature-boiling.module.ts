import { NgModule } from '@angular/core';
import { TemperatureBoilingComponent } from './temperature-boiling.component';
import {CommonModule} from "@angular/common";

@NgModule({
  declarations: [ TemperatureBoilingComponent ],
  imports: [
    CommonModule
  ],
  exports: [ TemperatureBoilingComponent ]
})
export class TemperatureBoilingModule { }
