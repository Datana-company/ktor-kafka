import {Component, OnInit, Input, Inject} from '@angular/core';
import {configProvide, IWebsocketService} from './websocket';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {TemperatureModel} from "./models/temperature.model.ts";

@Component({
  selector: 'lib-datana-temperature-view',
  templateUrl: './temperature-view.component.html',
  styleUrls: ['./temperature-view.component.css']
})
export class TemperatureViewComponent implements OnInit {

  tempStream$: Observable<string>;
  scale = 'C';

  constructor(
    @Inject(configProvide) private wsService: IWebsocketService
  ) { }

  ngOnInit(): void {
    this.tempStream$ = this.wsService.on('temperature-update').pipe(
      map((data: any) => {
        const tempStruct = new TemperatureModel(
          data?.temperature as number,
          new Date(data?.timeMillis as number),
          data?.durationMillis as number,
          data?.
        );
        return tempStruct;
      })
    );
  }

  displayTemp(temp: TemperatureModel): string {
    return temp.displayTemp(this.scale);
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
