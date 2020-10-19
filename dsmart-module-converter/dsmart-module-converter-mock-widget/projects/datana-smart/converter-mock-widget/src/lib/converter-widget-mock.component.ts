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

  selectedCase: String

  mockList: Array<MockListItemModel> = new Array<MockListItemModel>();
  // public mockList: Array<MockListItemModel> = [
  //       new MockListItemModel(
  //           'Каталог №1',
  //           'case1'
  //       ),
  //       new MockListItemModel(
  //           'Каталог №2',
  //           'case1'
  //       ),
  //       new MockListItemModel(
  //           'Каталог №3',
  //           'case1'
  //       ),
  //       new MockListItemModel(
  //           'Каталог №4',
  //           'case1'
  //       ),
  //       new MockListItemModel(
  //           '777',
  //           'case1'
  //       ),
  //       new MockListItemModel(
  //           'Каталог №123456789',
  //           'case1'
  //       ),
  //       new MockListItemModel(
  //           'Оооооооооочень_длиииииииииинноооооое_имяяяяяяя_каталооооога',
  //           'case1'
  //       ),
  //       new MockListItemModel(
  //           'Проект (last version) +++ final - v.3 - для печати',
  //           'case1'
  //       ),
  //       new MockListItemModel(
  //           'Новая папка',
  //           'case1'
  //       ),
  //       new MockListItemModel(
  //           'New folder',
  //           'case1'
  //       )
  //   ];

  constructor(private service: ConverterWidgetMockService) {
  }

    ngOnInit(): void {
      this.service.getList().pipe(
        takeUntil(this._unsubscribe)
      ).subscribe(data => {this.mockList = data?.cases})
    }

  ngOnDestroy(): void {
    this._unsubscribe.next();
    this._unsubscribe.complete();
  }
}
