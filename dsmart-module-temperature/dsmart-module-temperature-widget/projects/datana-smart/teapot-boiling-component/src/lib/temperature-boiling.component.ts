import {Component, Input, OnInit} from '@angular/core';

@Component({
  // tslint:disable-next-line:component-selector
  selector: 'temperature-boiling-component',
  templateUrl: './temperature-boiling.component.html',
  styleUrls: ['./temperature-boiling.component.css']
})
export class TemperatureBoilingComponent implements OnInit {

  @Input() time: string;

  constructor() {
  }

  ngOnInit(): void {
    this.collapsibleInit();
  }

  collapsibleInit = () => {
    const button = document.querySelector('.widget-collapsible-button');
    button.addEventListener('click', () => {
      button.previousElementSibling.classList.toggle('content-active');
    });
  }
}
