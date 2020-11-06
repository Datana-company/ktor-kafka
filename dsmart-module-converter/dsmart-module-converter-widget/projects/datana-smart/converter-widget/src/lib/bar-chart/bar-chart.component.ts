import {Component, Input, NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {NgxChartsModule} from '@swimlane/ngx-charts';
import {SlagRateModel} from "../models/slag-rate.model";

@Component({
    selector: 'bar-chart-component',
    templateUrl: './bar-chart.component.html',
    styleUrls: ['./bar-chart.component.scss']
})
export class BarChartComponent {

  // public xAxisTickFormattingFn = this.xAxisTickFormatting.bind(this);
  // public xAxisTickFormattingFn = (value) => `${value.toString()+"%"}`;
 // public xAxisTickFormattingFn = value => `X ${value.toLocaleString()}`;
 //    @Input() slagRateModel: SlagRateModel;
  view: any[] = [800, 200];

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
  barPadding='0px';
  barChartData: any;

  colorScheme = {
    domain: ['#4E80B2', '#BDE9E3', '#C23557']
  };

  @Input() set slagRateModel(dat: SlagRateModel) {
    this.barChartData = [
      {
        'name': 'Шлак',
        'value': (dat?.slagRate || 0) * 100
      },
      {
        'name': 'Допустимая доля металла',
        'value': 20
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
// xAxisTickFormatting(value: any){
  //   return value + " %" ;
  // }
  // value.toLocaleString()
}
