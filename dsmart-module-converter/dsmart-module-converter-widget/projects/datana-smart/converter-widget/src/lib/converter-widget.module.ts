import {APP_INITIALIZER, ModuleWithProviders, NgModule} from '@angular/core';
import {ConverterWidgetComponent} from './converter-widget.component';
import {VideoPlayerComponent} from './video-player-component/video-player.component';
import {ConverterWidgetRoutingModule} from './converter-widget-routing.module';
import {CommonModule} from '@angular/common';
import {WebsocketModule} from '@datana-smart/websocket';
import {HttpClient, HttpClientModule} from '@angular/common/http';
import {
  ConfigServiceConfig,
  configServiceConfig,
  ConfigServiceModule,
  ConfigServiceService
} from '@datana-smart/config-service';
import {NgxChartsModule} from '@swimlane/ngx-charts';
import {BarChartComponent} from './bar-chart/bar-chart.component';


@NgModule({
  declarations: [
    ConverterWidgetComponent,
    VideoPlayerComponent,
    BarChartComponent,
  ],
  imports: [
    WebsocketModule,
    ConfigServiceModule,
    HttpClientModule,
    CommonModule,
    ConverterWidgetRoutingModule.config(),
    NgxChartsModule
  ],
  providers: [
  ],
  exports: [ConverterWidgetComponent]
})

export class ConverterWidgetModule {}
