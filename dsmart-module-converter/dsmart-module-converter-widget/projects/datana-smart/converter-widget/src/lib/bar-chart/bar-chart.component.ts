import {Component, Input, NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {NgxChartsModule} from '@swimlane/ngx-charts';
import {ConverterModel} from "../models/converter.model";

@Component({
    selector: 'bar-chart-component',
    templateUrl: './bar-chart.component.html',
    styleUrls: ['./bar-chart.component.css']
})
export class BarChartComponent {

    @Input() converterData: ConverterModel;

    view: any[] = [600, 200];

    // options
    showXAxis: boolean = false;
    showYAxis: boolean = true;
    gradient: boolean = false;
    showLegend: boolean = false;
    showXAxisLabel: boolean = false;
    yAxisLabel: string = '';
    showYAxisLabel: boolean = false;
    xAxisLabel: string = '';
    showDataLabel: boolean = true;

    colorScheme = {
        domain: ['#C5C5C5', '#CBF76F', '#FF8740', '#AAAAAA']
    };

    constructor() {
        Object.assign(this, this.mapData());
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
                "value": this.converterData?.slagRate*100
            },
            {
                "name": "Допустимая доля металла",
                "value": 20
            },
            {
                "name": "Металл",
                "value": this.converterData?.steelRate*100
            }
        ];
    }
}
