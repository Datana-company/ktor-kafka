import {Component, Input, OnInit} from '@angular/core';

@Component({
  // tslint:disable-next-line:component-selector
  selector: 'temperature-boiling-component',
  templateUrl: './temperature-boiling.component.html',
  styleUrls: ['./temperature-boiling.component.css']
})
export class TemperatureBoilingComponent implements OnInit {

  @Input() durationToBoil: string;
  @Input() timeBack: Date;
  @Input() timeProc: Date;
  @Input() timeMl: Date;

  constructor() {
  }

  ngOnInit(): void {
  }

}
