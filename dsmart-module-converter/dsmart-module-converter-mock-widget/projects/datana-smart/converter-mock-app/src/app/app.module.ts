import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';

import { AppComponent } from './app.component';
import { ConverterWidgetMockModule } from '@datana-smart/converter-mock-widget';
import {environment} from "../environments/environment";

@NgModule({
    declarations: [
        AppComponent
    ],
    imports: [
        BrowserModule,
        FormsModule,
        HttpClientModule,
        ConverterWidgetMockModule.config({
          baseUrl: environment.host
        }),
    ],
    providers: [],
    bootstrap: [AppComponent]
})
export class AppModule { }
