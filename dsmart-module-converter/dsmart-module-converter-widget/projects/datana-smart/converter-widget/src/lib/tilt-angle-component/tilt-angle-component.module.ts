import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {TiltAngleComponentComponent} from "./tilt-angle-component.component";

@NgModule({
  declarations: [TiltAngleComponentComponent],
  imports: [
    CommonModule
  ],
  exports: [ TiltAngleComponentComponent]
})
export class TiltAngleComponentModule { }
