import {Component, Input, OnInit} from '@angular/core';

@Component({
  // tslint:disable-next-line:component-selector
  selector: 'collapsible-table',
  templateUrl: 'collapsible-table.component.html',
  styleUrls: ['collapsible-table.component.css']
})
export class CollapsibleTableComponent implements OnInit {

  private tp;

  @Input() timeBack: Date;

  @Input() timeProc: Date;

  @Input() timeMl: Date;

  constructor() {
  }

  isValidDate = dateObject => new Date(dateObject)
    .toString() !== 'Invalid Date'

  // isDateValid(d: Date): boolean {
  //   return d instanceof Date && !isNaN(d);
  // }

  get timeProcDelta(): string {
    return this.calculateTimeDelta(this.timeProc);
  }

  get timeMlDelta(): string {
    return this.calculateTimeDelta(this.timeMl);
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

  calculateTimeDelta = (time: Date) => {
    if (this.timeBack && time) {
      const mins = Math.floor((this.timeBack.getTime() - time.getTime()) / 60000.0);
      const secs = Math.floor((this.timeBack.getTime() - time.getTime()) / 1000.0) - mins * 60;
      return `${mins}m ${secs}s`;
    }
    return '';
  }

}
