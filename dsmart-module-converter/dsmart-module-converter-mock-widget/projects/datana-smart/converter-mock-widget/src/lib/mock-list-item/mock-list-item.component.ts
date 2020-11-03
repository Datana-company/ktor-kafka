import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {MockListItemModel} from "../models/mock-list-item-model";
import {ConverterWidgetMockService} from "../converter-widget-mock.service";
import {Subject} from "rxjs";
import {takeUntil} from "rxjs/operators";

@Component({
    selector: 'mock-list-item-component',
    templateUrl: './mock-list-item.component.html',
    styleUrls: ['./mock-list-item.component.css']
})
export class MockListItemComponent implements OnInit, OnDestroy {

    _unsubscribe = new Subject<void>();

    @Input() mockListItem: MockListItemModel;

    @Output() selectedCase = new EventEmitter<String>();
    @Output() startedCase = new EventEmitter<String>();

    selectedItemCssClassName = "widget-mock-list-item-selected"
    itemCssSelectionClass = ""

    constructor(private service: ConverterWidgetMockService) {
    }

    ngOnInit(): void {
    }

    startCase() {
        console.log(" --- MockListItemComponent::startCase()")
        this.service.startCase(this.mockListItem.name).pipe(
            takeUntil(this._unsubscribe)
        ).subscribe(data => {
            console.log(data);
            this.startedCase.emit(this.mockListItem.name);
        });
    }

    selectCase() {
        console.log(" --- MockListItemComponent::selectCase()")
        this.itemCssSelectionClass = this.selectedItemCssClassName;
        this.selectedCase.emit(this.mockListItem.name);
    }

    unselectCase(newCaseName) {
        console.log(" --- MockListItemComponent::unselectCase() --- newCaseName: " + newCaseName)
        if (newCaseName != this.mockListItem.name) {
            this.itemCssSelectionClass = "";
        }
    }

    ngOnDestroy(): void {
        this._unsubscribe.next();
        this._unsubscribe.complete();
    }
}
