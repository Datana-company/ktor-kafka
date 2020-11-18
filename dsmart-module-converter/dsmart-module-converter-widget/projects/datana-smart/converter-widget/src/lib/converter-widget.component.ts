import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {combineLatest, Subject} from "rxjs";
import {filter, map, takeUntil} from 'rxjs/operators';
import {configProvide, IWebsocketService} from '@datana-smart/websocket';
import {EventModel} from "./models/event.model";
import {SlagRateModel} from "./models/slag-rate.model";
import {ConverterFrameModel} from "./models/converter-frame.model";
import {ConverterMeltInfoModel} from "./models/converter-melt-info.model";
import {ConverterMeltModeModel} from "./models/converter-melt-mode.model";
import {ConverterMeltDevicesModel} from "./models/converter-melt-devices.model";
import {EventCategoryModel} from "./models/event-category.model";
import {ExecutionStatusModel} from "./models/event-execution-status.model";
import {ConverterAnglesModel} from "./models/converter-angles.model";
import {ConverterStateModel} from "./models/converter-state.model";
import {SlagRateChartModel} from "./models/slag-rate-chart.model";
import {SignalerLevelModel} from "./models/signaler-level.model";
import {SignalerSoundModel} from "./models/signaler-sound.model";
import {SignalerSoundTypeModel} from "./models/signaler-sound-type.model";
import {SignalerModel} from "./models/signaler.model";

@Component({
  selector: 'datana-converter-widget',
  templateUrl: './converter-widget.component.html',
  styleUrls: ['./converter-widget.component.css']
})
export class ConverterWidgetComponent implements OnInit, OnDestroy {

  _unsubscribe = new Subject<void>();

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

  constructor(
    @Inject(configProvide) private wsService: IWebsocketService
  ) { }

  ngOnInit(): void {
    this.playlist = 'http://camera.d.datana.ru/playlist.m3u8'

    const rawState = this.wsService.on('converter-state-update').pipe(
      takeUntil(this._unsubscribe),
      map((data: any) => {
        return new ConverterStateModel(
          data?.meltInfo as ConverterMeltInfoModel,
          data?.events as Array<EventModel>,
          data?.warningPoint as number
        );
      })
    );

    rawState.subscribe(data => {
      this.converterMeltInfoData = data?.meltInfo;
      // this.converterEvents = data?.events;
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
    });

    const rawSlagRate = this.wsService.on('converter-slag-rate-update').pipe(
      takeUntil(this._unsubscribe),
      map((data: any) => {
        return new SlagRateModel(
          data?.steelRate as number,
          data?.slagRate as number
        );
      })
    )

    rawSlagRate.subscribe(data => {
        this.converterSlagRateData = data;
      }
    )

    combineLatest([rawState, rawSlagRate]).pipe(
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

    const rawFrames = this.wsService.on('converter-frame-update').pipe(
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

    rawFrames.pipe(
      filter(frame => frame.channel == 'CAMERA')
    ).subscribe(data => {
      this.converterFrameCameraData = data;
    })

    rawFrames.pipe(
      filter(frame => frame.channel == 'MATH')
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
            data?.sound?.interval as number
          )
        );
      })
    ).subscribe(data => {
      this.converterSignalerLevel = data.level;
      this.converterSignalerSound = data.sound;
    });

/////// Для теста светофора /////////////////////////////////////////////////////////
//     document.getElementById('info').addEventListener('click', () => {
//       console.log('info');
//       this.converterSignalerLevel = SignalerLevelModel.INFO
//     });
//     document.getElementById('warning').addEventListener('click', () => {
//       console.log('warning');
//       this.converterSignalerLevel = SignalerLevelModel.WARNING
//     });
//     document.getElementById('critical').addEventListener('click', () => {
//       console.log('critical');
//       this.converterSignalerLevel = SignalerLevelModel.CRITICAL
//     });
//
//     document.getElementById('sound0').addEventListener('click', () => {
//       console.log('sound0');
//       this.converterSignalerSound = new SignalerSoundModel(SignalerSoundTypeModel.NONE, 0)
//     });
//     document.getElementById('sound1').addEventListener('click', () => {
//       console.log('sound1');
//       this.converterSignalerSound = new SignalerSoundModel(SignalerSoundTypeModel.SOUND_1, 2000)
//     });
//     document.getElementById('sound2').addEventListener('click', () => {
//       console.log('sound2');
//       this.converterSignalerSound = new SignalerSoundModel(SignalerSoundTypeModel.SOUND_2, 5000)
//     });
//     document.getElementById('sound3').addEventListener('click', () => {
//       console.log('sound3');
//       this.converterSignalerSound = new SignalerSoundModel(SignalerSoundTypeModel.SOUND_3, 8000)
//     });
//////////////////////////////////////////////////////////////////////////////////////////
  }

  ngOnDestroy(): void {
    this._unsubscribe.next();
    this._unsubscribe.complete();
  }

}
