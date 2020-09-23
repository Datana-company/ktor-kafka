import {Component, Inject, OnInit} from '@angular/core';
import {configProvide, IWebsocketService} from './websocket';
import {Observable} from 'rxjs';
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

  temperatureStream$: Observable<TemperatureModel>;
  analysisStream$: Observable<AnalysisModel>;
  scale = 'C';

  status = false;
  time = '2:54';
  temperature = 68.1;
  history: Array<RecommendationModel> = [
    new RecommendationModel(
      new Date('2020-09-21T12:45:30'),
      'Чайник вот вот взорвётся, выключите его!'
    ),
    new RecommendationModel(
      new Date('2020-09-21T12:45:30'),
      'Кто-то включил чайник'
    ),
  ];

  recommendation: RecommendationModel = new RecommendationModel(
    new Date('2020-09-21T12:45:30'),
    'Чайник вот вот взорвётся, выключите его!'
  );

  constructor(
    @Inject(configProvide) private wsService: IWebsocketService
  ) { }

  ngOnInit(): void {
    this.temperatureStream$ = this.wsService.on('temperature-update').pipe(
      map((data: any) => {
        console.log('DATA-temperature', data);
        return new TemperatureModel(
          data?.temperature as number,
          new Date(data?.timeMillis as number),
          data?.durationMillis as number,
          data?.deviationPositive as number,
          data?.deviationNegative as number,
        );
      })
    );
    this.analysisStream$ = this.wsService.on('temperature-analysis').pipe(
      map((data: any) => {
        console.log('DATA-analysis', data);
        return new AnalysisModel(
          new Date(data?.boilTime as number),
          new AnalysisStateModel(
            data?.state?.id,
            data?.state?.name,
            data?.state?.message
          )
        );
      })
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
