import {Component, Inject, OnInit} from '@angular/core';
import {configProvide, IWebsocketService} from './websocket';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {TemperatureModel} from './models/temperature.model';

@Component({
  // tslint:disable-next-line:component-selector
  selector: 'datana-temperature-view',
  templateUrl: './temperature-view.component.html',
  styleUrls: ['./temperature-view.component.css']
})
export class TemperatureViewComponent implements OnInit {

  dataStream$: Observable<TemperatureModel>;
  scale = 'C';

  constructor(
    @Inject(configProvide) private wsService: IWebsocketService
  ) { }

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
