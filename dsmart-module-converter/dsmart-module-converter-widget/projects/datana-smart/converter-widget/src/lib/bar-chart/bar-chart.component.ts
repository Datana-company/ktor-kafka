import {Component, Input, NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {NgxChartsModule} from '@swimlane/ngx-charts';
import {BarChartData} from './bar-chart-data';
import {ConverterModel} from "../models/converter.model";

@Component({
    selector: 'bar-chart-component',
    templateUrl: './bar-chart.component.html',
    styleUrls: ['./bar-chart.component.css']
})
export class BarChartComponent {

    @Input() converterData: ConverterModel;

    converterDataTest: any[] = [
        {
            "name": "Шлак",
            "value": 60
        },
        {
            "name": "Допустимая доля металла",
            "value": 20
        },
        {
            "name": "Металл",
            "value": 40
        }
    ];

    view: any[] = [700, 400];

    // options
    showXAxis: boolean = true;
    showYAxis: boolean = true;
    gradient: boolean = false;
    showLegend: boolean = false;
    showXAxisLabel: boolean = false;
    yAxisLabel: string = '';
    showYAxisLabel: boolean = false;
    xAxisLabel: string = '';

    colorScheme = {
        domain: ['#5AA454', '#A10A28', '#C7B42C', '#AAAAAA']
    };

    constructor() {
        Object.assign(this, this.converterDataTest);
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

    mapData(): any[] {
        return [
            {
                "name": "Шлак",
                "value": 60//this.converterData?.slagRate
            },
            {
                "name": "Допустимая доля металла",
                "value": 20
            },
            {
                "name": "Металл",
                "value": 40//this.converterData?.steelRate
            }
        ];
    }
}
