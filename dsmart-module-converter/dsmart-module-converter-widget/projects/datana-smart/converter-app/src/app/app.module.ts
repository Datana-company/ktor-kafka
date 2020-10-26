import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AppComponent } from './app.component';
import { AppRoutingModule } from "./app-routing.module";
import {WebsocketModule} from "@datana-smart/websocket";
import {environment} from "../environments/environment";
import {ConfigServiceModule} from "@datana-smart/config-service";

@NgModule({
  declarations: [
    AppComponent

  ],
  imports: [
    BrowserModule,
    FormsModule,
    WebsocketModule.config({
      url: environment.ws
    }),
    // ConfigServiceModule.forRoot({restWsUrl: 'http://localhost:8080/front-config'}),
    AppRoutingModule.config({
      url: environment.ws
    }),
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }

