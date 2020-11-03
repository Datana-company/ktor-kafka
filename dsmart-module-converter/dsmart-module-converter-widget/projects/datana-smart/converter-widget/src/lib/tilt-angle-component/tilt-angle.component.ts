import {Component, Input} from '@angular/core';
import {ConverterAnglesModel} from '../models/converter-angles-model'


@Component({
  selector: 'tilt-angle-component',
  templateUrl: './tilt-angle.component.html',
  styleUrls: ['./tilt-angle.component.css']
})
export class TiltAngleComponent  {

  @Input() angleData: ConverterAnglesModel;

}
