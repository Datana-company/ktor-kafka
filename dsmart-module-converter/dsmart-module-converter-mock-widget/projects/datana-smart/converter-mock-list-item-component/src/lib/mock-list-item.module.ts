import { NgModule } from '@angular/core';
import { MockListItemComponent } from './mock-list-item.component';
import {CommonModule} from "@angular/common";



@NgModule({
  declarations: [MockListItemComponent],
  imports: [
    CommonModule
  ],
  exports: [
    MockListItemComponent
  ]
})
export class MockListItemModule { }
