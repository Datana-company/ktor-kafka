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

  constructor(
    @Inject(configProvide) private wsService: IWebsocketService
  ) { }

  ngOnInit(): void {
    this.tempStream$ = this.wsService.on('temperature-update').pipe(
      map((data: any) => {
        const tempNum = (data?.temperature as number); // ?.toPrecision(1);
        return tempNum?.toFixed(1);
      })
    );
  }

}
