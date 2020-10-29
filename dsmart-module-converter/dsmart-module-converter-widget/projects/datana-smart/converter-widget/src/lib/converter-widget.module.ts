import {NgModule} from '@angular/core';
import {ConverterWidgetComponent} from './converter-widget.component';
import {VideoPlayerComponent} from './video-player-component/video-player.component';
import {ConverterWidgetRoutingModule} from "./converter-widget-routing.module";
import {CommonModule} from "@angular/common";
import {WebsocketModule} from "@datana-smart/websocket";
import {TiltAngleComponent} from "./tilt-angle-component/tilt-angle.component";
import {EventRecommendationComponent} from "./event-recommendation-component/event-recommendation.component";

@NgModule({
  declarations: [
    ConverterWidgetComponent,
    VideoPlayerComponent,
    TiltAngleComponent,
    EventRecommendationComponent
  ],
  imports: [
    WebsocketModule,
    CommonModule,
    ConverterWidgetRoutingModule,
  ],
  exports: [ConverterWidgetComponent]
})
export class ConverterWidgetModule {
}
