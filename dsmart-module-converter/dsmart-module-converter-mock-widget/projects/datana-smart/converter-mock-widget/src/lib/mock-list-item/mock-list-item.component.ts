import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {MockListItemModel} from "../models/mock-list-item-model";
import {Subject} from "rxjs";
import {takeUntil} from "rxjs/operators";
import {HostService} from "../services/host.service";

@Component({
    selector: 'mock-list-item-component',
    templateUrl: './mock-list-item.component.html',
    styleUrls: ['./mock-list-item.component.css']
})
export class MockListItemComponent implements OnInit, OnDestroy {

    private unsubscribe = new Subject<void>();

    @Input() mockListItem: MockListItemModel;

    @Output() selectedCase = new EventEmitter<string>();
    @Output() startedCase = new EventEmitter<string>();

    selectedItemCssClassName = "widget-mock-list-item-selected";
    itemCssSelectionClass = "";

    constructor(private service: HostService) {
    }

    ngOnInit(): void {
    }

    startCase(): void {
        console.log(" --- MockListItemComponent::startCase()");
        this.service.startCase(this.mockListItem.name).pipe(
            takeUntil(this.unsubscribe)
        ).subscribe(data => {
            console.log(data);
            this.startedCase.emit(this.mockListItem.name);
        });
    }

    selectCase(): void {
        console.log(" --- MockListItemComponent::selectCase()");
        this.itemCssSelectionClass = this.selectedItemCssClassName;
        this.selectedCase.emit(this.mockListItem.name);
    }

    unselectCase(newCaseName): void {
        console.log(" --- MockListItemComponent::unselectCase() --- newCaseName: " + newCaseName);
        if (newCaseName !== this.mockListItem.name) {
            this.itemCssSelectionClass = "";
        }
    }

    ngOnDestroy(): void {
        this.unsubscribe.next();
        this.unsubscribe.complete();
    }
}
