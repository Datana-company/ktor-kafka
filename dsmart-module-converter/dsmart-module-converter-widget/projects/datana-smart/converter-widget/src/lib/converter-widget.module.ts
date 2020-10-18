import { NgModule } from '@angular/core';
import { ConverterWidgetComponent } from './converter-widget.component';
import { VideoPlayerComponent } from './video-player-component/video-player.component';
import {ConverterWidgetRoutingModule} from "./converter-widget-routing.module";
import {CommonModule} from "@angular/common";
import {WebsocketModule} from "@datana-smart/websocket";



@NgModule({
  declarations: [ConverterWidgetComponent, VideoPlayerComponent],
  imports: [
    WebsocketModule,
    CommonModule,
    ConverterWidgetRoutingModule
  ],
  exports: [ConverterWidgetComponent]
})
export class ConverterWidgetModule { }
