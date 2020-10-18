import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {Subject} from "rxjs";
import {map, takeUntil} from 'rxjs/operators';
import {configProvide, IWebsocketService} from '@datana-smart/websocket';
import {RecommendationModel} from "./models/recommendation.model";
import {ConverterModel} from "./models/converter.model";
import {ConverterMeltInfoModel} from "./models/converter-melt-info.model";

@Component({
  selector: 'datana-converter-widget',
  templateUrl: './converter-widget.component.html',
  styles: [
  ]
})
export class ConverterWidgetComponent implements OnInit, OnDestroy {

  _unsubscribe = new Subject<void>();

  public converterData: ConverterModel;
  public recommendations: Array<RecommendationModel> = new Array<RecommendationModel>();

  playlist: string;

  constructor(
    @Inject(configProvide) private wsService: IWebsocketService
  ) { }

  ngOnInit(): void {
    this.playlist = "http://camera.d.datana.ru/playlist.m3u8"

    this.wsService.on('converter-update').pipe(
      takeUntil(this._unsubscribe),
      map((data: any) => {
        return new ConverterModel(
          data?.frameId as string,
          data?.frameTime as number,
          data?.framePath as string,
          data?.meltInfo as ConverterMeltInfoModel,
          data?.angle as number,
          data?.steelRate as number,
          data?.slagRate as number
        );
      })
    ).subscribe(data => {
      this.converterData = data;
    });

    this.wsService.on('recommendation-update').pipe(
      takeUntil(this._unsubscribe),
      map((data: any) => {
        return new RecommendationModel(
          new Date(data?.time as number),
          data?.title as string,
          data?.textMessage as string
        );
      })
    ).subscribe(data => {
      this.recommendations.push(data);
    });

  }

  ngOnDestroy(): void {
    this._unsubscribe.next();
    this._unsubscribe.complete();
  }

}
