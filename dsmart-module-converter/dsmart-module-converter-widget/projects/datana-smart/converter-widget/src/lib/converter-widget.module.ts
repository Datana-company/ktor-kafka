import { NgModule } from '@angular/core';
import { ConverterWidgetComponent } from './converter-widget.component';
import { VideoPlayerComponent } from '@datana-smart/video-player-component';



@NgModule({
  declarations: [ConverterWidgetComponent],
  imports: [
    VideoPlayerComponent
  ],
  exports: [ConverterWidgetComponent]
})
export class ConverterWidgetModule { }
