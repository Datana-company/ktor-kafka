import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppComponent} from './app.component';
import {environment} from '../environments/environment';
import {TemperatureViewModule} from '@datana-smart/dsmart-module-temperature-widget';
import {FormsModule} from '@angular/forms';
import {WebsocketModule} from './websocket';

@NgModule({
  declarations: [
    AppComponent,
  ],
  imports: [
    BrowserModule,
    WebsocketModule.config({
      url: environment.ws
    }),
    TemperatureViewModule.config({
      url: environment.ws
    }),
    // TemperatureViewModule,
    FormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {
}
