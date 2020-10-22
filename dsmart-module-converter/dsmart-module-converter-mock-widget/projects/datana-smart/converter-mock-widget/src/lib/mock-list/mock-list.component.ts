import {Component, Input, OnInit, Output, EventEmitter} from '@angular/core';
import {MockListItemModel} from "../models/mock-list-item-model";

@Component({
    selector: 'mock-list-component',
    templateUrl: './mock-list.component.html',
    styleUrls: ['./mock-list.component.css']
})
export class MockListComponent implements OnInit {

    @Input() mockList: Array<MockListItemModel>;

    @Output('selectedCase')
    transitionalSelectedCase = new EventEmitter<String>();

    constructor() {
    }

    ngOnInit(): void {
    }
}
