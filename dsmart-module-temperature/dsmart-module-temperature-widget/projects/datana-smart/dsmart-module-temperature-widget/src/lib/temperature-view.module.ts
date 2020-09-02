import {ModuleWithProviders, NgModule} from '@angular/core';
import { TemperatureViewComponent } from './temperature-view.component';
import {WebSocketConfig, WebsocketModule, config} from './websocket';
import {CommonModule} from '@angular/common';
import {RouterModule} from "@angular/router";

@NgModule({
  declarations: [TemperatureViewComponent],
    imports: [
        WebsocketModule,
        CommonModule,
        RouterModule,
    ],
  providers: [
    TemperatureViewModule,
  ],

  exports: [TemperatureViewComponent],
})
export class TemperatureViewModule {
  public static config(wsConfig: WebSocketConfig): ModuleWithProviders<TemperatureViewModule> {
    return {
      ngModule: TemperatureViewModule,
      providers: [{ provide: config, useValue: wsConfig }]
    };
  }
  // public static config(wsConfig: WebSocketConfig): ModuleWithProviders<WebsocketModule> {
  //   return {
  //     ngModule: WebsocketModule,
  //     providers: [{ provide: config, useValue: wsConfig }]
  //   };
  // }
}
