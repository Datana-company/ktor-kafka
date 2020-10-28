import {NgModule} from '@angular/core';
import {ConverterWidgetComponent} from './converter-widget.component';
import {VideoPlayerComponent} from './video-player-component/video-player.component';
import {BarChartComponent} from './bar-chart/bar-chart.component';
import {ConverterWidgetRoutingModule} from "./converter-widget-routing.module";
import {CommonModule} from "@angular/common";
import {WebsocketModule} from "@datana-smart/websocket";
import {TiltAngleComponentModule} from "./tilt-angle-component/tilt-angle-component.module";
import {EventRecommendationModule} from "./event-recommendation-component/event-recommendation.module";
import {NgxChartsModule} from '@swimlane/ngx-charts';

@NgModule({
  declarations: [
    ConverterWidgetComponent,
    VideoPlayerComponent,
    BarChartComponent
  ],
  imports: [
    WebsocketModule,
    CommonModule,
    ConverterWidgetRoutingModule,
    TiltAngleComponentModule,
    EventRecommendationModule,
    NgxChartsModule
  ],
  exports: [ConverterWidgetComponent]
})
export class ConverterWidgetModule {
}
