import {Component, Inject, OnInit} from '@angular/core';
import {configProvide, IWebsocketService} from '@datana-smart/websocket';
import {merge, Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {TemperatureModel} from './models/temperature.model';
import {RecommendationModel} from "@datana-smart/recommendation-component";
import {AnalysisModel, AnalysisStateModel} from "./models/analysis.model";

@Component({
  // tslint:disable-next-line:component-selector
  selector: 'datana-temperature-view',
  templateUrl: './temperature-view.component.html',
  styleUrls: ['./temperature-view.component.css']
})
export class TemperatureViewComponent implements OnInit {

  private temperatureStream$: Observable<TemperatureModel>;
  private analysisStream$: Observable<AnalysisModel>;
  /**
   * Текущая температура от процессинга
   */
  temperatureCurrentStream$: Observable<number>;

  /**
   * Длительность времени до закипания
   */
  durationToBoil$: Observable<string>;

  /**
   * Состояние чайника: идентификатор
   */
  stateIdStream$: Observable<string>;

  /**
   * Состояние чайника: Название
   */
  stateNameStream$: Observable<string>;

  /**
   * Текущее время по версии Бэкенда UI
   */
  timeBackStream$: Observable<Date>;

  /**
   * Время пакета данных по версии процессинга
   */
  timeProcStream$: Observable<Date>;

  /**
   * Время пакета данных по версии ML
   */
  timeMlStream$: Observable<Date>;

  scale = 'C';
  history: Array<RecommendationModel> = [
    new RecommendationModel(
      new Date('2020-09-21T12:45:30'),
      'Чайник вот вот взорвётся, выключите его!'
    ),
    new RecommendationModel(
      new Date('2020-09-21T11:25:13'),
      'Кто-то включил чайник'
    ),
    new RecommendationModel(
      new Date('2020-09-21T11:05:10'),
      'Чайник вот вот взорвётся, выключите его!'
    ),
    new RecommendationModel(
      new Date('2020-09-16T05:55:31'),
      'Кто-то включил чайник'
    ),
    new RecommendationModel(
      new Date('2020-09-21T05:51:53'),
      'Чайник вот вот взорвётся, выключите его!'
    ),
    new RecommendationModel(
      new Date('2020-09-21T04:02:05'),
      'Кто-то включил чайник'
    )
  ];

  constructor(
    @Inject(configProvide) private wsService: IWebsocketService
  ) {
  }

  ngOnInit(): void {
    this.temperatureStream$ = this.wsService.on('temperature-update').pipe(
      map((data: any) => {
        // console.log('DATA-temperature', data);
        return new TemperatureModel(
          data?.temperatureAverage as number,
          new Date(data?.timeBackend as number),
          new Date(data?.timeLatest as number),
          new Date(data?.timeEarliest as number),
          data?.durationMillis as number,
          data?.deviationPositive as number,
          data?.deviationNegative as number,
        );
      })
    );
    this.analysisStream$ = this.wsService.on('temperature-analysis').pipe(
      map((data: any) => {
        // console.log('DATA-analysis', data);
        return new AnalysisModel(
          new Date(data?.timeBackend as number),
          new Date(data?.timeActual as number),
          data?.durationToBoil as number,
          data?.sensorId as string,
          data?.temperatureLast as number,
          new AnalysisStateModel(
            data?.state?.id,
            data?.state?.name,
            data?.state?.message
          )
        );
      })
    );
    this.timeBackStream$ = merge(
      this.temperatureStream$.pipe(map((obj) => obj.timeBackend)),
      this.analysisStream$.pipe(map((obj) => obj.backendTime)),
    );
    this.durationToBoil$ = this.analysisStream$.pipe(
      map(({actualTime, backendTime, durationToBoil, state}: AnalysisModel) => {
        return state.id === 'switchedOn'
          ? actualTime.getTime() + durationToBoil - backendTime.getTime()
          : null;
      }),
      map(duration => {
        if (duration == null) { return null; }
        const durationSecs = duration / 1000;
        const mins = Math.round(durationSecs / 60);
        const secs = Math.round(durationSecs % 60);
        return `${mins}m ${secs}s`;
      })
    );
    this.timeMlStream$ = this.analysisStream$.pipe(
      map(({actualTime}) => actualTime)
    );
    this.timeProcStream$ = this.temperatureStream$.pipe(
      map(({timeLatest}) => {
        console.log("setting timeStart", timeLatest);
        return timeLatest;
      })
    );
    this.temperatureCurrentStream$ = this.temperatureStream$.pipe(
      map(({temperature}) => temperature)
    );
    this.stateIdStream$ = this.analysisStream$.pipe(
      map(({state}) => state.id)
    );
    this.stateNameStream$ = this.analysisStream$.pipe(
      map(({state}) => state.name)
    );
  }

  setKelvin(event: Event): void {
    event.preventDefault();
    this.scale = 'K';
  }

  setCelsius(event: Event): void {
    event.preventDefault();
    this.scale = 'C';
  }

  setFarenheit(event: Event): void {
    event.preventDefault();
    this.scale = 'F';
  }
}
