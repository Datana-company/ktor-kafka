import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { AppComponent } from './app.component';
import { ConverterWidgetMockModule } from '@datana-smart/converter-mock-widget';

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    ConverterWidgetMockModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
