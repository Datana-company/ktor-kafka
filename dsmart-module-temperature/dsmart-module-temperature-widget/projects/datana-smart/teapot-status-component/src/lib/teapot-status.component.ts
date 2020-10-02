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
    return this.status === undefined || this.status === null ? 'status-indicator-unknown'
      : this.status ? 'status-indicator-on' : 'status-indicator-off';
  }

  constructor() {
  }

  ngOnInit(): void {
  }
}
