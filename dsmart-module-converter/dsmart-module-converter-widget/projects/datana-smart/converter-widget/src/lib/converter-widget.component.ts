import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {combineLatest, interval, scheduled, Subject, timer} from 'rxjs';
import {filter, map, takeUntil} from 'rxjs/operators';
import {configProvide, IWebsocketService} from '@datana-smart/websocket';
import {EventModel} from './models/event.model';
import {SlagRateModel} from './models/slag-rate.model';
import {ConverterFrameModel} from './models/converter-frame.model';
import {ConverterMeltInfoModel} from './models/converter-melt-info.model';
import {ConverterMeltModeModel} from './models/converter-melt-mode.model';
import {ConverterMeltDevicesModel} from './models/converter-melt-devices.model';
import {EventCategoryModel} from './models/event-category.model';
import {ExecutionStatusModel} from './models/event-execution-status.model';
import {ConverterAnglesModel} from './models/converter-angles.model';
import {ConverterStateModel} from './models/converter-state.model';
import {SlagRateChartModel} from './models/slag-rate-chart.model';
import {SignalerLevelModel} from './models/signaler-level.model';
import {SignalerSoundModel} from './models/signaler-sound.model';
import {SignalerSoundTypeModel} from './models/signaler-sound-type.model';
import {SignalerModel} from './models/signaler.model';

@Component({
  selector: 'datana-converter-widget',
  templateUrl: './converter-widget.component.html',
  styleUrls: ['./converter-widget.component.scss']
})
export class ConverterWidgetComponent implements OnInit, OnDestroy {

  _unsubscribe = new Subject<void>();
  public current_time = interval(1000)
    .pipe(
      // document.lastModified может быть близким приближением к текущему времени сервера
      map(() => new Date(document.lastModified))
    );
  public converterMeltInfoData: ConverterMeltInfoModel;
  public converterSlagRateData: SlagRateModel;
  public converterFrameCameraData: ConverterFrameModel;
  public converterFrameMathData: ConverterFrameModel;
  public converterAnglesData: ConverterAnglesModel;
  public converterEvents: Array<EventModel> = new Array<EventModel>();
  public converterSlagRateChart: SlagRateChartModel;
  public converterSignalerLevel: SignalerLevelModel;
  public converterSignalerSound: SignalerSoundModel;
  playlist: string;
  irCameraId;
  irCameraName;

  constructor(
    @Inject(configProvide) private wsService: IWebsocketService
  ) {
  }

  ngOnInit(): void {
    this.playlist = 'http://camera.d.datana.ru/playlist.m3u8'

    const observableRawState = this.wsService.on('converter-state-update').pipe(
      takeUntil(this._unsubscribe),
      map((data: any) => {
        return new ConverterStateModel(
          data?.meltInfo as ConverterMeltInfoModel,
          data?.events as Array<EventModel>,
          data?.warningPoint as number
        );
      })
    );

    observableRawState.subscribe(data => {
      this.converterMeltInfoData = data?.meltInfo;
      this.irCameraId = this.converterMeltInfoData?.devices?.irCamera?.id;
      this.irCameraName = this.converterMeltInfoData?.devices?.irCamera?.name;
    });

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
      this.irCameraId = this.converterMeltInfoData?.devices?.irCamera?.id;
      this.irCameraName = this.converterMeltInfoData?.devices?.irCamera?.name;
    });

    const observableRawSlagRate = this.wsService.on('converter-slag-rate-update').pipe(
      takeUntil(this._unsubscribe),
      map((data: any) => {
        return new SlagRateModel(
          data?.steelRate as number,
          data?.slagRate as number
        );
      })
    )

    observableRawSlagRate.subscribe(data => {
        this.converterSlagRateData = data;
      }
    )

    combineLatest([observableRawState, observableRawSlagRate]).pipe(
      map((data: any) => {
        return new SlagRateChartModel(
          data[1]?.steelRate as number,
          data[1]?.slagRate as number,
          data[0]?.warningPoint as number
        )
      })
    ).subscribe(data => {
      this.converterSlagRateChart = data;
    });

    const observableRawFrames = this.wsService.on('converter-frame-update').pipe(
      takeUntil(this._unsubscribe),
      map((data: any) => {
        return new ConverterFrameModel(
          data?.frameId as string,
          data?.frameTime as number,
          data?.framePath as string,
          data?.image as string,
          data?.channel as string
        );
      })
    )

    observableRawFrames.pipe(
      filter(frame => frame.channel === 'CAMERA')
    ).subscribe(data => {
      this.converterFrameCameraData = data;
    })

    observableRawFrames.pipe(
      filter(frame => frame.channel === 'MATH')
    ).subscribe(data => {
      this.converterFrameMathData = data;
    })

    this.wsService.on('converter-angles-update').pipe(
      takeUntil(this._unsubscribe),
      map((data: any) => {
        return new ConverterAnglesModel(
          data.angleTime as number,
          data?.angle?.toFixed(0) as number,
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

    this.wsService.on('signaler-update').pipe(
      takeUntil(this._unsubscribe),
      map((data: any) => {
        return new SignalerModel(
          data?.level as SignalerLevelModel,
          new SignalerSoundModel(
            data?.sound?.type as SignalerSoundTypeModel,
            data?.sound?.interval as number,
            data?.sound?.timeout as number
          )
        );
      })
    ).subscribe(data => {
      this.converterSignalerLevel = data.level;
      this.converterSignalerSound = data.sound;
    });

  }

  get converterDeveiceName() {
    const converterDeveiceName = this.converterMeltInfoData?.devices?.converter?.name;
    return converterDeveiceName ? converterDeveiceName.toString()
      .concat(': ', this.converterMeltInfoData?.devices?.converter?.id.toString()) : 'Конвертер -';
  }

  get converterMeltShiftNumber() {
    const shiftNumber = this.converterMeltInfoData?.shiftNumber
    return shiftNumber ? shiftNumber : '-'
  }

  get converterMeltCrewNumber() {
    const crewNumber = this.converterMeltInfoData?.crewNumber
    return crewNumber ? crewNumber : '-'
  }

  get converterMeltSteelGrade() {
    const steelGrade = this.converterMeltInfoData?.steelGrade
    return steelGrade ? steelGrade : '-'
  }

  get converterMeltInfoDataMeltNumber() {
    const meltNumber = this.converterMeltInfoData?.meltNumber
    return meltNumber ? meltNumber : '-'
  }

  ngOnDestroy(): void {
    this._unsubscribe.next();
    this._unsubscribe.complete();
  }
}
