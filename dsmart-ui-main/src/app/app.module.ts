import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import {Routes, RouterModule} from '@angular/router';
import {FormsModule} from "@angular/forms";

import {AppComponent} from './app.component';
import {WebsocketModule} from "./websocket";
import {environment} from '../environments/environment';
import { AppComponent } from './app.component';
import { HomeComponent } from './home/home.component';
import { TemperatureViewModule} from "temperature-view";
import { SensorDataComponent } from './sensor-data/sensor-data.component';

const routes: Routes =[
  { path: '', component: HomeComponent },
  { path: 'sensor-data', component: SensorDataComponent }
];

@NgModule({
    declarations: [
        AppComponent
    ],
    imports: [
        BrowserModule,
        WebsocketModule.config({
            url: environment.ws
        })
    ],
    providers: [],
    bootstrap: [AppComponent]
  declarations: [
    AppComponent,
    HomeComponent,
    SensorDataComponent
  ],
  imports: [
    BrowserModule,
    RouterModule.forRoot(routes),
    TemperatureViewModule,
    FormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {
}
