import {Component, Inject, Input, OnInit} from '@angular/core';
import {TemperatureModel} from "../models/temperature.model";


@Component({
  selector: 'tilt-angle-component',
  templateUrl: './tilt-angle-component.component.html',
  styleUrls: ['./tilt-angle-component.component.css']
})
export class TiltAngleComponentComponent  {

  @Input() temperatureModel: TemperatureModel;

}
