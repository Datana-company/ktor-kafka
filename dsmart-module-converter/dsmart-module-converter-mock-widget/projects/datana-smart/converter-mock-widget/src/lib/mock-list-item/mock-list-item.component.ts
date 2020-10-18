import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {MockListItemModel} from "../models/mock-list-item-model";
import {ConverterWidgetMockService} from "../converter-widget-mock.service";
import {Observable, Subject} from "rxjs";
import {takeUntil} from "rxjs/operators";

@Component({
  // tslint:disable-next-line:component-selector
  selector: 'mock-list-item-component',
  templateUrl: './mock-list-item.component.html',
  styleUrls: ['./mock-list-item.component.css']
})
export class MockListItemComponent implements OnInit, OnDestroy {

  _unsubscribe = new Subject<void>();

  @Input() mockListItem: MockListItemModel;

  constructor(private service: ConverterWidgetMockService) { }

  ngOnInit(): void {
  }

  startCase() {
      this.service.startCase(this.mockListItem.dir).pipe(
        takeUntil(this._unsubscribe)
      ).subscribe(data => console.log(data));
  }

  ngOnDestroy(): void {
    this._unsubscribe.next();
    this._unsubscribe.complete();
  }

}
