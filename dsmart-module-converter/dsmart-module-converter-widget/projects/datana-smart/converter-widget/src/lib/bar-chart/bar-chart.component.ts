import {Component, Input, NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {NgxChartsModule} from '@swimlane/ngx-charts';
import {SlagRateModel} from "../models/slag-rate.model";

@Component({
  selector: 'bar-chart-component',
  templateUrl: './bar-chart.component.html',
  styleUrls: ['./bar-chart.component.css']
})
export class BarChartComponent {

  view: any[] = [600, 200];

  // options
  showXAxis = false;
  showYAxis = true;
  gradient = false;
  showLegend = false;
  showXAxisLabel = false;
  yAxisLabel = '';
  showYAxisLabel = false;
  xAxisLabel = '';
  showDataLabel = true;

  barChartData: any;

  colorScheme = {
    domain: ['#C5C5C5', '#CBF76F', '#FF8740', '#AAAAAA']
  };

  @Input() set slagRateModel(dat: SlagRateModel) {
    this.barChartData = [
      {
        'name': 'Шлак',
        'value': (dat?.slagRate || 0) * 100
      },
      {
        'name': 'Допустимая доля металла',
        'value': (dat?.warningPoint || 0) * 100
      },
      {
        'name': 'Металл',
        'value': (dat?.steelRate || 0) * 100
      }
    ];
  }

  constructor() {
  }

  onSelect(data): void {
    console.log('Item clicked', JSON.parse(JSON.stringify(data)));
  }

  onActivate(data): void {
    console.log('Activate', JSON.parse(JSON.stringify(data)));
  }

  onDeactivate(data): void {
    console.log('Deactivate', JSON.parse(JSON.stringify(data)));
  }

}
