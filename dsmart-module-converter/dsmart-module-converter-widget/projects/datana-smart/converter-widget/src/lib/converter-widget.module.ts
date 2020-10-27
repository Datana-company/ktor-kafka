import {NgModule, Optional, SkipSelf} from '@angular/core';
import { ConverterWidgetComponent } from './converter-widget.component';
import { VideoPlayerComponent } from './video-player-component/video-player.component';
import {ConverterWidgetRoutingModule} from "./converter-widget-routing.module";
import {CommonModule} from "@angular/common";
import {WebsocketModule} from "@datana-smart/websocket";
import {ConfigServiceModule, ConfigServiceService} from "@datana-smart/config-service";
import {HttpClientModule} from "@angular/common/http";



@NgModule({
  declarations: [ConverterWidgetComponent, VideoPlayerComponent],
  imports: [
    WebsocketModule,
    HttpClientModule,
    CommonModule,
    ConfigServiceModule.forRoot({restWsUrl: 'http://localhost:8080/front-config'}),
    ConverterWidgetRoutingModule
  ],
  providers: [ ConfigServiceService],
  exports: [ConverterWidgetComponent]
})
export class ConverterWidgetModule {
  // constructor (@Optional() @SkipSelf() parentModule: ConfigServiceModule) {
  //   if (parentModule) {
  //     throw new Error(
  //       'ConfigServiceModule 22 is already loaded. Import it in the AppModule only');
  //   }
  // }
}
