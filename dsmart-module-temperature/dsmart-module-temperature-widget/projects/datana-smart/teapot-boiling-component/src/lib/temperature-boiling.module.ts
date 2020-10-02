import { NgModule } from '@angular/core';
import { TemperatureBoilingComponent } from './temperature-boiling.component';
import {CommonModule} from "@angular/common";
import {CollapsibleTableModule} from "@datana-smart/collapsible-table-component";

@NgModule({
  declarations: [ TemperatureBoilingComponent ],
  imports: [
    CommonModule,
    CollapsibleTableModule,
  ],
  exports: [ TemperatureBoilingComponent ]
})
export class TemperatureBoilingModule { }
