import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';

@Component({
  // tslint:disable-next-line:component-selector
  selector: 'teapot-status-component',
  templateUrl: './teapot-status.component.html',
  styleUrls: ['./teapot-status.component.css']
})
export class TeapotStatusComponent implements OnInit {

  @Input() status: boolean;

  @Input() statusName: string;

  _statusStyle: string;
  get statusStyle(): string {
    // return new Date().getSeconds() % 2 === 0 ? 'status-indicator-on' : 'status-indicator-off';
    return status === undefined || status === null ? 'status-indicator-unknown'
      : status ? 'status-indicator-on' : 'status-indicator-off';
  }

  constructor() {
  }

  ngOnInit(): void {
  }
}
