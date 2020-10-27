import { NgModule } from '@angular/core';
import { ConverterWidgetComponent } from './converter-widget.component';
import { VideoPlayerComponent } from './video-player-component/video-player.component';
import {ConverterWidgetRoutingModule} from "./converter-widget-routing.module";
import {CommonModule} from "@angular/common";
import {WebsocketModule} from "@datana-smart/websocket";
import {TiltAngleComponentModule} from "./tilt-angle-component/tilt-angle-component.module";



@NgModule({
  declarations: [ConverterWidgetComponent, VideoPlayerComponent],
  imports: [
    WebsocketModule,
    CommonModule,
    ConverterWidgetRoutingModule,
    TiltAngleComponentModule
  ],
  exports: [ConverterWidgetComponent]
})
export class ConverterWidgetModule { }
