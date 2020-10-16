import { NgModule } from '@angular/core';
import { MockListComponent } from './mock-list.component';
import { MockListItemModule } from '@datana-smart/converter-mock-list-item-component';
import {CommonModule} from "@angular/common";

@NgModule({
  declarations: [MockListComponent],
  imports: [
    CommonModule,
    MockListItemModule
  ],
  exports: [MockListComponent]
})
export class MockListModule { }
