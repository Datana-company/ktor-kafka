import { NgModule } from '@angular/core';
import { ConverterWidgetComponent } from './converter-widget.component';
import { VideoPlayerComponent } from './video-player-component/video-player.component';



@NgModule({
  declarations: [ConverterWidgetComponent, VideoPlayerComponent],
  imports: [
  ],
  exports: [ConverterWidgetComponent]
})
export class ConverterWidgetModule { }
