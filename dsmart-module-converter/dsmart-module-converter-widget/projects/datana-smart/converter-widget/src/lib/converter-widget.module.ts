import {LOCALE_ID, NgModule} from '@angular/core';
import {ConverterWidgetComponent} from './converter-widget.component';
import {VideoPlayerComponent} from './video-player-component/video-player.component';
import {BarChartComponent} from './bar-chart/bar-chart.component';
import {ConverterWidgetRoutingModule} from './converter-widget-routing.module';
import {CommonModule, registerLocaleData} from '@angular/common';
import {WebsocketModule} from '@datana-smart/websocket';
import {TiltAngleComponent} from './tilt-angle-component/tilt-angle.component';
import {EventRecommendationComponent} from './event-recommendation-component/event-recommendation.component';
import {NgxChartsModule} from '@swimlane/ngx-charts';
import { SignalerComponent } from './signaler/signaler.component';
import localeRu from '@angular/common/locales/ru';

registerLocaleData(localeRu);

@NgModule({
  declarations: [
    ConverterWidgetComponent,
    VideoPlayerComponent,
    TiltAngleComponent,
    EventRecommendationComponent,
    BarChartComponent,
    SignalerComponent
  ],
  providers: [
      { provide: LOCALE_ID, useValue: 'ru' }
  ],
  imports: [
    WebsocketModule,
    CommonModule,
    ConverterWidgetRoutingModule,
    NgxChartsModule
  ],
  exports: [ConverterWidgetComponent]
})
export class ConverterWidgetModule {
}
