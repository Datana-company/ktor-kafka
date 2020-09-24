import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import {TemperatureViewModule} from "@datana-smart/temperature-widget";
import {WebsocketModule} from "@datana-smart/websocket";
import {environment} from "../environments/environment";

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    WebsocketModule.config({
      url: environment.ws
    }),
    TemperatureViewModule.config({
      url: environment.ws
    }),
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
