import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppComponent} from './app.component';
import {WebsocketModule} from './websocket';
import {environment} from '../environments/environment';
import {TemperatureViewModule} from '@datana-smart/dsmart-module-temperature-widget';
import {SensorDataComponent} from './sensor-data/sensor-data.component';
import {FormsModule} from '@angular/forms';

@NgModule({
    declarations: [
        AppComponent,
        SensorDataComponent
    ],
    imports: [
        BrowserModule,
        WebsocketModule.config({
            url: environment.ws
        }),
        TemperatureViewModule,
        FormsModule
    ],
    providers: [],
    bootstrap: [AppComponent]
})
export class AppModule {
}
