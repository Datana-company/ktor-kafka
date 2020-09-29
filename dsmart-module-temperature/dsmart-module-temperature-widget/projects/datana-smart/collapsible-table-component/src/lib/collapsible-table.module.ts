import { NgModule } from '@angular/core';
import { CollapsibleTableComponent } from './collapsible-table.component';
import {CommonModule} from "@angular/common";

@NgModule({
  declarations: [ CollapsibleTableComponent ],
  imports: [
    CommonModule
  ],
  exports: [ CollapsibleTableComponent ]
})
export class CollapsibleTableModule { }
