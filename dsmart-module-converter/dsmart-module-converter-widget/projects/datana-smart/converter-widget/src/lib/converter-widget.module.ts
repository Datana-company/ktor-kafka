import { NgModule } from '@angular/core';
import { ConverterWidgetComponent } from './converter-widget.component';
import { VideoPlayerModule } from '@datana-smart/video-player-component';



@NgModule({
  declarations: [ConverterWidgetComponent],
  imports: [
    VideoPlayerModule
  ],
  exports: [ConverterWidgetComponent]
})
export class ConverterWidgetModule { }
