import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {ConverterWidgetComponent} from "./converter-widget.component";

const routes: Routes = [
  {
    path: '',
    component: ConverterWidgetComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ConverterWidgetRoutingModule { }
