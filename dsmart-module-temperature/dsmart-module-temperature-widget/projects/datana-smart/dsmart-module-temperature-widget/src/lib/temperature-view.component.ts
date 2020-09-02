import {Component, OnInit, Input, Inject} from '@angular/core';
import {configProvide, IWebsocketService} from './websocket';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';

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
        const tempNum = data?.temperature as number;
        return this.displayTemp(tempNum);
      })
    );
  }

  displayTemp(temp: number): string {
    if (temp == null) { return 'NaN'; }
    let tempScaled: number;
    switch (this.scale) {
      case 'C': { tempScaled = temp - 273.15; break; }
      case 'F': { tempScaled = (temp - 273.15) * 9.0 / 5.0 + 32.0; break; }
      default: { this.scale = 'K'; tempScaled = temp; break; }
    }
    return tempScaled?.toFixed(1) || 'NaN';
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
