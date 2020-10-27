import { Component, OnInit } from '@angular/core';
import {TemperatureModel} from "../models/temperature.model";

@Component({
  selector: 'lib-tilt-angle-component',
  templateUrl: './tilt-angle-component.component.html',
  styleUrls: ['./tilt-angle-component.component.css']
})
export class TiltAngleComponentComponent implements OnInit {
  public temperatureModel: TemperatureModel;

  constructor() { }

  ngOnInit(): void {

  }

}
