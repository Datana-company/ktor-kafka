import {Component, OnInit} from '@angular/core';
import {MockListItemModel} from "./models/mock-list-item-model";

@Component({
    selector: 'datana-converter-mock-widget',
    templateUrl: './converter-mock-widget.component.html',
    styleUrls: ['./converter-mock-widget.component.css']
})
export class ConverterWidgetMockComponent implements OnInit {

    public mockList: Array<MockListItemModel> = [
        new MockListItemModel(
            'Каталог №1',
            'case1'
        ),
        new MockListItemModel(
            'Каталог №2',
            'case1'
        ),
        new MockListItemModel(
            'Каталог №3',
            'case1'
        ),
        new MockListItemModel(
            'Каталог №4',
            'case1'
        ),
        new MockListItemModel(
            'Каталог №5',
            'case1'
        ),
        new MockListItemModel(
            'Каталог №123456789',
            'case1'
        ),
        new MockListItemModel(
            'Оооооооооочень_длиииииииииинноооооое_имяяяяяяя_каталооооога',
            'case1'
        ),
        new MockListItemModel(
            'Проект (last version) +++ final - v.3 - для печати',
            'case1'
        ),
        new MockListItemModel(
            'Новая папка',
            'case1'
        ),
        new MockListItemModel(
            'New folder',
            'case1'
        )
    ];

    constructor() {
    }

    ngOnInit(): void {
    }

}
