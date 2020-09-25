import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';

@Component({
  // tslint:disable-next-line:component-selector
  selector: 'teapot-status-component',
  templateUrl: './teapot-status.component.html',
  styleUrls: ['./teapot-status.component.css']
})
export class TeapotStatusComponent implements OnInit, OnChanges {

  @Input() status: boolean;
  @Input() statusName: string;
  statusStyle: string;

  constructor() {
  }

  ngOnInit(): void {
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.statusStyle = status === undefined || status === null ? 'status-indicator-unknown'
      : status ? 'status-indicator-on' : 'status-indicator-off';
  }

}
