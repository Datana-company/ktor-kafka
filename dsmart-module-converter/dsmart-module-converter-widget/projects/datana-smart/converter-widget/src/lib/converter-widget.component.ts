import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {Subject} from "rxjs";
import {map, takeUntil} from 'rxjs/operators';
import {configProvide, IWebsocketService} from '@datana-smart/websocket';
import {EventModel} from "./models/event-model";
import {SlagRateModel} from "./models/slag-rate.model";
import {ConverterFrameModel} from "./models/converter-frame.model";
import {ConverterMeltInfoModel} from "./models/converter-melt-info.model";
import {ConverterMeltModeModel} from "./models/converter-melt-mode.model";
import {ConverterMeltDevicesModel} from "./models/converter-melt-devices.model";
import {EventCategoryModel} from "./models/event-category.model";
import {ExecutionStatusModel} from "./models/event-execution-status.model";
import {ConverterAnglesModel} from "./models/converter-angles-model";

@Component({
  selector: 'datana-converter-widget',
  templateUrl: './converter-widget.component.html',
  styleUrls: ['./converter-widget.component.css']
})
export class ConverterWidgetComponent implements OnInit, OnDestroy {

  _unsubscribe = new Subject<void>();

  public converterMeltInfoData: ConverterMeltInfoModel;
  public converterSlagRateData: SlagRateModel;
  public converterFrameData: ConverterFrameModel;
  public converterAnglesData: ConverterAnglesModel;
  public converterEvents: Array<EventModel> = new Array<EventModel>();

  playlist: string;

  constructor(
    @Inject(configProvide) private wsService: IWebsocketService
  ) { }

  ngOnInit(): void {
    this.playlist = 'http://camera.d.datana.ru/playlist.m3u8'

    this.wsService.on('converter-melt-info-update').pipe(
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
      this.converterMeltInfoData = data;
    });

    this.wsService.on('converter-slag-rate-update').pipe(
      takeUntil(this._unsubscribe),
      map((data: any) => {
        return new SlagRateModel(
          data?.steelRate as number,
          data?.slagRate as number
        );
      })
    ).subscribe(data => {
      this.converterSlagRateData = data;
    });

    this.wsService.on('converter-frame-update').pipe(
      takeUntil(this._unsubscribe),
      map((data: any) => {
        return new ConverterFrameModel(
          data?.frameId as string,
          data?.frameTime as number,
          data?.framePath as string
        );
      })
    ).subscribe(data => {
      this.converterFrameData = data;
    });

    this.wsService.on('converter-angles-update').pipe(
      takeUntil(this._unsubscribe),
      map((data: any) => {
        return new ConverterAnglesModel(
          data?.angle?.toFixed(1) as number,
          data?.source as number
        );
      })
    ).subscribe(data => {
      this.converterAnglesData = data;
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
          event?.isActive as boolean,
          event?.executionStatus as ExecutionStatusModel
        )
      ) as Array<EventModel>)
    ).subscribe(data => {
      this.converterEvents = data;
    });
  }

  public isCompleted(executionStatus: ExecutionStatusModel) {
    return executionStatus == ExecutionStatusModel.COMPLETED;
  }

  public isFailed(executionStatus: ExecutionStatusModel) {
    return executionStatus == ExecutionStatusModel.FAILED;
  }

  public isWarning(eventCategoryModel: EventCategoryModel) {
    return eventCategoryModel == EventCategoryModel.CRITICAL || eventCategoryModel == EventCategoryModel.WARNING;
  }

  public isInfo(eventCategoryModel: EventCategoryModel) {
    return eventCategoryModel == EventCategoryModel.INFO || eventCategoryModel == EventCategoryModel.HINT;
  }

  ngOnDestroy(): void {
    this._unsubscribe.next();
    this._unsubscribe.complete();
  }

}
