import {Component, Input, NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {NgxChartsModule} from '@swimlane/ngx-charts';
import {SlagRateChartModel} from '../models/slag-rate-chart.model';

@Component({
    selector: 'bar-chart-component',
    templateUrl: './bar-chart.component.html',
    styleUrls: ['./bar-chart.component.scss']
})
export class BarChartComponent {
  view: any[] = [950, 140];

  // options
  showXAxis = true;
  showYAxis = true;
  gradient = false;
  showLegend = false;
  showXAxisLabel = true;
  yAxisLabel = '';
  showYAxisLabel = false;
  xAxisLabel = '';
  maxXAxisTickLength = 10;
  xAxisTics = [];
  trimXAxisTicks = true;
  showDataLabel = true;
  barPadding = 5;
  barChartData: any;

  colorScheme = {
    // domain: ['#4E80B27f', '#BDE9E37f', '#C235577f']
    domain: ['#4E80B27f', '#C235577f']
  };
  dataLabelFormatting = (value) => `${value.toString() + '  %' }`;
  tickFormat = (o: any) => `<span class=""><span>${o}</span><span>%</span></span>`

  @Input() set slagRateChartModel(dat: SlagRateChartModel) {
    const tempSlagRate = ((dat?.slagRate || 0) * 100);
    const tempSlagRateAkt = ((tempSlagRate % 1) === 0.5 ? Math.floor(tempSlagRate) : Math.round(tempSlagRate))
    const warnPoint = (dat?.warningPoint || 0) * 100;
    this.xAxisTics = [warnPoint]
    this.barChartData = [
      {
        'name': 'Шлак',
        'value': tempSlagRateAkt
      },
      // {
      //   'name': 'Допустимая доля металла',
      //   'value': (dat?.warningPoint || 0) * 100
      // },
      {
        'name': 'Металл',
        'value': Math.round((dat?.steelRate || 0) * 100)
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
