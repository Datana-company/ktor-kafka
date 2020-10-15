import {Component, Inject, OnInit} from '@angular/core';
import {ConverterModel} from "./models/converter.model";
import {Observable} from "rxjs";
import {map} from 'rxjs/operators';
import {configProvide, IWebsocketService} from '@datana-smart/websocket';

@Component({
  selector: 'datana-converter-widget',
  templateUrl: './converter-widget.component.html',
  styles: [
  ]
})
export class ConverterWidgetComponent implements OnInit {

  private converterStream$: Observable<ConverterModel>;

  /**
   * Текущий угол наклона конвертера
   */
  tiltAngleStream$: Observable<number>;

  playlist: string;

  constructor(
    @Inject(configProvide) private wsService: IWebsocketService
  ) { }

  ngOnInit(): void {
    this.playlist = "http://camera.d.datana.ru/playlist.m3u8"

    this.converterStream$ = this.wsService.on('converter-update').pipe(
      map((data: any) => {
        return new ConverterModel(
          data?.tiltAngle as number
        );
      })
    );

    this.tiltAngleStream$ = this.converterStream$.pipe(
      map(({tiltAngle}) => tiltAngle)
    );
  }

}
