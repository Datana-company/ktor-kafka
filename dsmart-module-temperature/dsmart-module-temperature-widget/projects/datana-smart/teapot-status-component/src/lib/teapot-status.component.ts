import {Component, Input, OnInit} from '@angular/core';

@Component({
  // tslint:disable-next-line:component-selector
  selector: 'teapot-status-component',
  templateUrl: './teapot-status.component.html',
  styleUrls: ['./teapot-status.component.css']
})
export class TeapotStatusComponent implements OnInit {

  @Input() status: boolean;

  constructor() { }

  ngOnInit(): void {
  }

}
