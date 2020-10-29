import {Component, Input} from '@angular/core';
import {TemperatureModel} from "../models/temperature.model";


@Component({
  selector: 'tilt-angle-component',
  templateUrl: './tilt-angle.component.html',
  styleUrls: ['./tilt-angle.component.css']
})
export class TiltAngleComponent  {

  @Input() temperatureModel: TemperatureModel;

}
