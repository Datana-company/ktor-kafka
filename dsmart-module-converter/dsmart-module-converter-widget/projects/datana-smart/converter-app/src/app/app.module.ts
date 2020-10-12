import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { AppComponent } from './app.component';
import { ConverterWidgetModule } from '@datana-smart/converter-widget';

@NgModule({
  declarations: [
    AppComponent

  ],
  imports: [
    BrowserModule,
    FormsModule,
    ConverterWidgetModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
