import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AppComponent } from './app.component';
import { AppRoutingModule } from "./app-routing.module";
import {WebsocketModule} from "@datana-smart/websocket";
import {environment} from "../environments/environment";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";

@NgModule({
  declarations: [
    AppComponent

  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    FormsModule,
    WebsocketModule.config({
      url: environment.ws
    }),
    AppRoutingModule.config(
      {url: environment.ws},
      {restWsUrl: 'http://localhost:8080/front-config'}
    ),
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
