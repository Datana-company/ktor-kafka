import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {Subject} from "rxjs";
import {map, takeUntil} from 'rxjs/operators';
import {configProvide, IWebsocketService} from '@datana-smart/websocket';
import {RecommendationModel} from "./models/recommendation.model";
import {TemperatureModel} from "./models/temperature.model";
import {ConverterModel} from "./models/converter.model";
import {ConverterVideoModel} from "./models/converter-video.model";
import {ConverterMeltInfoModel} from "./models/converter-melt-info.model";
import {ConverterMeltModeModel} from "./models/converter-melt-mode.model";
import {ConverterMeltDevicesModel} from "./models/converter-melt-devices.model";
import {RecommendationCategoryModel} from "./models/recommendation-category.model";

@Component({
  selector: 'datana-converter-widget',
  templateUrl: './converter-widget.component.html',
  styleUrls: ['./converter-widget.component.css']
})
export class ConverterWidgetComponent implements OnInit, OnDestroy {

  _unsubscribe = new Subject<void>();

  public temperatureModel: TemperatureModel;
  public converterData: ConverterModel;
  public converterVideoData: ConverterVideoModel;
  public converterMetaData: ConverterMeltInfoModel;
  public recommendations: Array<RecommendationModel> = new Array<RecommendationModel>();

  playlist: string;

  constructor(
    @Inject(configProvide) private wsService: IWebsocketService
  ) { }

  ngOnInit(): void {
    this.playlist = "http://camera.d.datana.ru/playlist.m3u8"

    this.wsService.on('temperature-update').pipe(
      takeUntil(this._unsubscribe),
      map((data: any) => {
        return new TemperatureModel(
          data?.temperatureAverage?.toFixed(1) as number
        );
      })
    ).subscribe(data => {
      this.temperatureModel = data;
    });

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

    this.wsService.on('converter-video-update').pipe(
      takeUntil(this._unsubscribe),
      map((data: any) => {
        return new ConverterVideoModel(
          data?.frameId as string,
          data?.frameTime as number,
          data?.framePath as string,
          data?.meltInfo as ConverterMeltInfoModel
        );
      })
    ).subscribe(data => {
      this.converterVideoData = data;
    });

    this.wsService.on('converter-meta-update').pipe(
      takeUntil(this._unsubscribe),
      map((data: any) => {
        return new ConverterMeltInfoModel(
          data?.id as string,
          data?.timeStart as number,
          data?.meltNumber as string,
          data?.steelGrade as string,
          data?.crewNumber as string,
          data?.shiftNumber as string,
          data?.mode as ConverterMeltModeModel,
          data?.devices as ConverterMeltDevicesModel
        );
      })
    ).subscribe(data => {
      this.converterMetaData = data;
    });

    this.wsService.on('recommendations-update').pipe(
      takeUntil(this._unsubscribe),
      map((data: any) => data?.list.map(
        rec => new RecommendationModel(
          rec?.id as string,
          new Date(rec?.timeStart as number),
          new Date(rec?.timeFinish as number),
          rec?.title as string,
          rec?.textMessage as string,
          rec?.category as RecommendationCategoryModel,
          rec?.isActive as boolean
        )
      ) as Array<RecommendationModel>)
    ).subscribe(data => {
      this.recommendations = data;
    });
  }

  ngOnDestroy(): void {
    this._unsubscribe.next();
    this._unsubscribe.complete();
  }

}
