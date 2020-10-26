import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {Subject} from "rxjs";
import {map, takeUntil} from 'rxjs/operators';
import {configProvide, IWebsocketService} from '@datana-smart/websocket';
import {EventModel} from "./models/event-model";
import {TemperatureModel} from "./models/temperature.model";
import {ConverterModel} from "./models/converter.model";
import {ConverterVideoModel} from "./models/converter-video.model";
import {ConverterMeltInfoModel} from "./models/converter-melt-info.model";
import {ConverterMeltModeModel} from "./models/converter-melt-mode.model";
import {ConverterMeltDevicesModel} from "./models/converter-melt-devices.model";
import {EventCategoryModel} from "./models/event-category.model";
import {BarChartData} from "./bar-chart/bar-chart-data";

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
  public events: Array<EventModel> = new Array<EventModel>();
  public barChartData: BarChartData;

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
        console.log("------------------------------------------------");
        console.log("---" + data);
      this.converterData = data;
      // this.barChartData.slagRate = this.converterData.slagRate;
      // this.barChartData.steelRate = this.converterData.steelRate;
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

    this.wsService.on('events-update').pipe(
      takeUntil(this._unsubscribe),
      map((data: any) => data?.list.map(
        event => new EventModel(
          event?.id as string,
          new Date(event?.timeStart as number),
          new Date(event?.timeFinish as number),
          event?.title as string,
          event?.textMessage as string,
          event?.category as EventCategoryModel,
          event?.isActive as boolean
        )
      ) as Array<EventModel>)
    ).subscribe(data => {
      this.events = data;
    });
  }

  ngOnDestroy(): void {
    this._unsubscribe.next();
    this._unsubscribe.complete();
  }

}
