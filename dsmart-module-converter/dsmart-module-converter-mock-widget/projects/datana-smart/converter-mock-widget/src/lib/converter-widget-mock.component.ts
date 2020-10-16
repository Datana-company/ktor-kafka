import { Component, OnInit } from '@angular/core';
import {MockListItemModel} from "@datana-smart/converter-mock-list-item-component";

@Component({
  selector: 'datana-converter-mock-widget',
    templateUrl: './converter-mock-widget.component.html',
    styleUrls: ['./converter-mock-widget.component.css']
})
export class ConverterWidgetMockComponent implements OnInit {

    mockList: Array<MockListItemModel> = [
        new MockListItemModel(
            new Date('2020-09-21T12:45:30'),
            'Каталог №1'
        ),
        new MockListItemModel(
            new Date('2020-09-21T11:25:13'),
            'Каталог №2'
        ),
        new MockListItemModel(
            new Date('2020-09-21T11:05:10'),
            'Каталог №3'
        ),
        new MockListItemModel(
            new Date('2020-09-16T05:55:31'),
            'Каталог №4'
        ),
        new MockListItemModel(
            new Date('2020-09-21T05:51:53'),
            'Каталог №5'
        ),
        new MockListItemModel(
            new Date('2020-09-21T04:02:05'),
            'Каталог №123456789'
        ),
        new MockListItemModel(
            new Date('2020-09-21T05:51:53'),
            'Оооооооооочень_длиииииииииинноооооое_имяяяяяяя_каталооооога'
        ),
        new MockListItemModel(
            new Date('2020-09-21T04:02:05'),
            'Проект (last version) +++ final - v.3 - для печати'
        ),
        new MockListItemModel(
            new Date('2020-09-21T05:51:53'),
            'Новая папка'
        ),
        new MockListItemModel(
            new Date('2020-09-21T04:02:05'),
            'New folder'
        )
    ];

  constructor() { }

  ngOnInit(): void {
  }

}
