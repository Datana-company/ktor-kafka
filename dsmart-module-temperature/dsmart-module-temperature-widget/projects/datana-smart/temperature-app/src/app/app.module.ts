import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import {TemperatureViewModule} from "@datana-smart/temperature-widget";

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    TemperatureViewModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
