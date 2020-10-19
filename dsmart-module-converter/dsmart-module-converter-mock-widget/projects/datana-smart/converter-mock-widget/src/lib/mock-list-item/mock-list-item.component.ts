import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {MockListItemModel} from "../models/mock-list-item-model";
import {ConverterWidgetMockService} from "../converter-widget-mock.service";
import {ConverterWidgetMockComponent} from "../converter-widget-mock.component";
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

  constructor(private service: ConverterWidgetMockService, private converterWidgetMockComponent: ConverterWidgetMockComponent) { }

  ngOnInit(): void {
  }

  startCase() {
      this.service.startCase(this.mockListItem.name).pipe(
        takeUntil(this._unsubscribe)
      ).subscribe(data => {
        console.log(data);
        this.converterWidgetMockComponent.selectedCase = this.mockListItem.name;
      });
  }

  ngOnDestroy(): void {
    this._unsubscribe.next();
    this._unsubscribe.complete();
  }

}
