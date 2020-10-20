import {Component, OnDestroy, OnInit} from '@angular/core';
import {MockListItemModel} from "./models/mock-list-item-model";
import {Subject} from "rxjs";
import {ConverterWidgetMockService} from "./converter-widget-mock.service";
import {takeUntil} from "rxjs/operators";

@Component({
    selector: 'datana-converter-mock-widget',
    templateUrl: './converter-mock-widget.component.html',
    styleUrls: ['./converter-mock-widget.component.css']
})
export class ConverterWidgetMockComponent implements OnInit, OnDestroy {

    _unsubscribe = new Subject<void>();

    selectedCase: String;

    mockList: Array<MockListItemModel> = new Array<MockListItemModel>();

    constructor(private service: ConverterWidgetMockService) {
    }

    ngOnInit(): void {
        this.service.getList().pipe(
            takeUntil(this._unsubscribe)
        ).subscribe(data => {
            this.mockList = data?.cases
        })
    }

    ngOnDestroy(): void {
        this._unsubscribe.next();
        this._unsubscribe.complete();
    }

    setSelectedCase(selectedCase) {
        this.selectedCase = selectedCase;
    }
}
