import {Component, Input, OnInit, Output, EventEmitter, ViewChild, ViewChildren, QueryList} from '@angular/core';
import {MockListItemModel} from "../models/mock-list-item-model";
import {MockListItemComponent} from "../mock-list-item/mock-list-item.component";

@Component({
    selector: 'mock-list-component',
    templateUrl: './mock-list.component.html',
    styleUrls: ['./mock-list.component.css']
})
export class MockListComponent implements OnInit {

    @Input() mockList: Array<MockListItemModel>;

    @Output('selectedCase')
    transitionalSelectedCase = new EventEmitter<String>();

    @Output('startedCase')
    transitionalStartedCase = new EventEmitter<String>();

    // @ViewChild(MockListItemComponent) mockListItem: MockListItemComponent;
    @ViewChildren(MockListItemComponent) mockListItems: QueryList<MockListItemComponent>;

    constructor() {
    }

    caseStarted(event) {
        console.log(" --- MockListComponent::caseStarted() --- event: " + event)
        this.transitionalStartedCase.emit(event)
    }

    newCaseSelected(event) {
        console.log(" --- MockListComponent::newCaseSelected() --- event: " + event)
        console.log(" --- mockListItems.length: " + this.mockListItems.length)
        this.transitionalSelectedCase.emit(event)
        this.mockListItems.forEach(item => item.unselectCase(event))
    }

    ngOnInit(): void {
    }
}
