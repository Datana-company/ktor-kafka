import {Component, Inject, OnInit} from '@angular/core';
import {TemperatureModel} from "../models/temperature.model";
import {map, takeUntil} from "rxjs/operators";
import {configProvide, IWebsocketService} from "@datana-smart/websocket";
import {Subject} from "rxjs";

@Component({
  selector: 'tilt-angle-component',
  templateUrl: './tilt-angle-component.component.html',
  styleUrls: ['./tilt-angle-component.component.css']
})
export class TiltAngleComponentComponent implements OnInit {

  _unsubscribe = new Subject<void>();
  public temperatureModel: TemperatureModel;

  constructor(
    @Inject(configProvide) private wsService: IWebsocketService
  ) { }

  ngOnInit(): void {
    this.wsService.on('temperature-update').pipe(
      takeUntil(this._unsubscribe),
      map((data: any) => {
        return new TemperatureModel(
          data?.temperatureAverage?.toFixed(1) as number
        );
      })
    ).subscribe(data => {
      this.temperatureModel = data;
    });
  }

}
