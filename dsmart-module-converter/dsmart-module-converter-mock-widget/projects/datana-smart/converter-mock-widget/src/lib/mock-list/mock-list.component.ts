import {Component, Input, OnInit} from '@angular/core';
import {MockListItemModel} from "../models/mock-list-item-model";

@Component({
  // tslint:disable-next-line:component-selector
  selector: 'mock-list-component',
  templateUrl: './mock-list.component.html',
  styleUrls: ['./mock-list.component.css']
})
export class MockListComponent implements OnInit {

  @Input() mockList: Array<MockListItemModel>;

  constructor() { }

  ngOnInit(): void {
  }

}
