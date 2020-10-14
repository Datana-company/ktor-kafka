import { NgModule } from '@angular/core';
import { ConverterWidgetComponent } from './converter-widget.component';
import { VideoPlayerComponent } from './video-player-component/video-player.component';
import {ConverterWidgetRoutingModule} from "./converter-widget-routing.module";



@NgModule({
  declarations: [ConverterWidgetComponent, VideoPlayerComponent],
  imports: [
    ConverterWidgetRoutingModule
  ],
  exports: [ConverterWidgetComponent]
})
export class ConverterWidgetModule { }
