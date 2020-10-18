import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppComponent} from './app.component';
import {environment} from '../environments/environment';
import {FormsModule} from '@angular/forms';
import {WebsocketModule} from './websocket';
import {AppRoutingModule} from "./app-routing.module";
import {NavigatePanelComponent} from "./navigate-panel/navigate-panel.component";

@NgModule({
  declarations: [
    AppComponent,
    NavigatePanelComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule.config({
      url: environment.ws
    }),
    WebsocketModule.config({
      url: environment.ws
    }),
    FormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {
}
