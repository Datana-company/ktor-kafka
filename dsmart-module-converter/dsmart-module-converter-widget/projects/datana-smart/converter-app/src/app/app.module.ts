import {BrowserModule} from '@angular/platform-browser';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {AppComponent} from './app.component';
import {AppRoutingModule} from "./app-routing.module";
import {WebsocketModule} from "@datana-smart/websocket";
import {environment} from "../environments/environment";

@NgModule({
    declarations: [
        AppComponent

    ],
    imports: [
        // BrowserModule и BrowserAnimationsModule должны быть в одном *.module.ts
        BrowserModule,
        BrowserAnimationsModule,
        FormsModule,
        WebsocketModule.config({
            url: environment.ws
        }),
        AppRoutingModule.config({
            url: environment.ws
        }),
    ],
    providers: [],
    bootstrap: [AppComponent]
})
export class AppModule {
}
