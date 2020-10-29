import { NgModule } from '@angular/core';
import { ConverterWidgetComponent } from './converter-widget.component';
import { VideoPlayerComponent } from './video-player-component/video-player.component';
import {ConverterWidgetRoutingModule} from './converter-widget-routing.module';
import {CommonModule} from '@angular/common';
import {WebsocketModule} from '@datana-smart/websocket';
import {HttpClientModule} from '@angular/common/http';
import {ConfigServiceModule, ConfigServiceService} from "@datana-smart/config-service";
import { NgxChartsModule } from '@swimlane/ngx-charts';



@NgModule({
  declarations: [ConverterWidgetComponent, VideoPlayerComponent],
  imports: [
    WebsocketModule,
    HttpClientModule,
    CommonModule,
    ConverterWidgetRoutingModule,
    NgxChartsModule
  ],
  providers: [ ConfigServiceService],
  exports: [ConverterWidgetComponent]
})
export class ConverterWidgetModule { }
