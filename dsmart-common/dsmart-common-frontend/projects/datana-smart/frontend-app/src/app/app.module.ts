import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { AppComponent } from './app.component';
import {ConfigServiceModule} from "@datana-smart/config-service";

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    ConfigServiceModule.forRoot({restWsUrl: 'http://localhost:8080/front-config'})
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
