import {ModuleWithProviders, NgModule} from '@angular/core';
import {TemperatureViewComponent} from './temperature-view.component';
import {WebSocketConfig, config, WebsocketModule} from '@datana-smart/websocket';
import {CommonModule} from '@angular/common';
import {RouterModule} from '@angular/router';
import {TeapotStatusModule} from '@datana-smart/teapot-status-component';
import {HistoryModule} from "@datana-smart/history-component";
import {TemperatureBoilingModule} from "@datana-smart/teapot-boiling-component";
import {TemperatureModule} from "@datana-smart/temperature-component";
import {RecommendationModule} from "@datana-smart/recommendation-component";

@NgModule({
  declarations: [TemperatureViewComponent],
  imports: [
    WebsocketModule.config({
      url: 'localhost:8080'
    }),
    CommonModule,
    RouterModule,
    TeapotStatusModule,
    RecommendationModule,
    HistoryModule,
    TemperatureBoilingModule,
    TemperatureModule,
  ],
  providers: [
    TemperatureViewModule,
  ],
  exports: [TemperatureViewComponent],
})
export class TemperatureViewModule {
  public static config(wsConfig: WebSocketConfig): ModuleWithProviders<TemperatureViewModule> {
    console.log('Setting up TemperatureViewModule', wsConfig);
    return {
      ngModule: TemperatureViewModule,
      providers: [{provide: config, useValue: wsConfig}]
    };
  }
}
