import { NgModule } from '@angular/core';
import { TeapotStatusComponent } from './teapot-status.component';
import { FormsModule } from "@angular/forms";

@NgModule({
  declarations: [TeapotStatusComponent],
  imports: [
    FormsModule
  ],
  exports: [ TeapotStatusComponent ]
})
export class TeapotStatusModule { }
