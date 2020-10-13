import {NgModule} from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {TemperatureViewComponent} from "./temperature-view.component";

const routes: Routes = [
  {
    path: '',
    component: TemperatureViewComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
