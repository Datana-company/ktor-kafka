import {Component, Input, OnInit} from '@angular/core';

@Component({
  // tslint:disable-next-line:component-selector
  selector: 'temperature-component',
  templateUrl: './temperature.component.html',
  styleUrls: ['./temperature.component.css']
})
export class TemperatureComponent implements OnInit {

  @Input() temperature: number;
  @Input() scale = 'C';

  constructor() { }

  ngOnInit(): void {
  }

  displayTemp(scale: string): string {
    const temp = this.temperature;
    if (temp == null) { return 'NaN'; }
    let tempScaled: number;
    switch (scale) {
      case 'C': { tempScaled = temp - 273.15; break; }
      case 'F': { tempScaled = (temp - 273.15) * 9.0 / 5.0 + 32.0; break; }
      default: { tempScaled = temp; break; }
    }
    return tempScaled?.toFixed(1) || 'NaN';
  }

}
