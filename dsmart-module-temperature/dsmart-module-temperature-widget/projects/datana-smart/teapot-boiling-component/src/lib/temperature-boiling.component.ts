import {Component, Input, OnInit} from '@angular/core';

@Component({
  // tslint:disable-next-line:component-selector
  selector: 'temperature-boiling-component',
  templateUrl: './temperature-boiling.component.html',
  styleUrls: ['./temperature-boiling.component.css']
})
export class TemperatureBoilingComponent implements OnInit {

  @Input() durationToBoil: string;
  @Input() timeCurrent: number;
  @Input() timeBackend: number;
  @Input() timeActual: number;

  get backendTimeDelta(): string {
    return this.calculateTimeDelta(this.timeBackend);
  }

  get actualTimeDelta(): string {
    return this.calculateTimeDelta(this.timeActual);
  }

  constructor() {
  }

  ngOnInit(): void {
    this.collapsibleInit();
  }

  collapsibleInit = () => {
    const button = document.querySelector('.widget-boiling-time .widget-collapsible-button');
    button.addEventListener('click', () => {
      button.previousElementSibling.classList.toggle('content-active');
    });
  }

  calculateTimeDelta = (time) => {
    const mins = Math.floor((this.timeCurrent - time) / 60000.0);
    const secs = Math.floor( (this.timeCurrent - time) / 1000.0 ) - mins * 60;
    return `${mins}m ${secs}s`;
  }
}
