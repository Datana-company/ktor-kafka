import { Component, OnInit } from '@angular/core';

@Component({
  // tslint:disable-next-line:component-selector
  selector: 'temperature-boiling-component',
  templateUrl: './temperature-boiling.component.html',
  styleUrls: ['./temperature-boiling.component.css']
})
export class TemperatureBoilingComponent implements OnInit {

  time = '2:54'

  constructor() { }

  ngOnInit(): void {
  }

}
