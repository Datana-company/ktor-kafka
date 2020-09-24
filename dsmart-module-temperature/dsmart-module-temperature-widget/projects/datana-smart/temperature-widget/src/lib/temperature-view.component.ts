import {Component, Inject, OnInit} from '@angular/core';
import {configProvide, IWebsocketService} from '@datana-smart/websocket';
import {Observable, of} from 'rxjs';
import {map} from 'rxjs/operators';
import {TemperatureModel} from './models/temperature.model';
import {RecommendationModel} from "@datana-smart/recommendation-component";

@Component({
  // tslint:disable-next-line:component-selector
  selector: 'datana-temperature-view',
  templateUrl: './temperature-view.component.html',
  styleUrls: ['./temperature-view.component.css']
})
export class TemperatureViewComponent implements OnInit {

  dataStream$: Observable<TemperatureModel>;
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
    this.dataStream$ = this.wsService.on('temperature-update').pipe(
      map((data: any) => {
        console.log('DATA', data);
        return new TemperatureModel(
          data?.temperature as number,
          new Date(data?.timeMillis as number),
          data?.durationMillis as number,
          data?.deviationPositive as number,
          data?.deviationNegative as number,
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
