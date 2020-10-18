import {Component, Input, OnInit} from '@angular/core';
import {MockListItemModel} from "../models/mock-list-item-model";

@Component({
  // tslint:disable-next-line:component-selector
  selector: 'mock-list-item-component',
  templateUrl: './mock-list-item.component.html',
  styleUrls: ['./mock-list-item.component.css']
})
export class MockListItemComponent implements OnInit {

  @Input() mockListItem: MockListItemModel;

  constructor() { }

  ngOnInit(): void {
  }

}
