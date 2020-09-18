import {Component, Input, OnInit} from '@angular/core';

@Component({
  // tslint:disable-next-line:component-selector
  selector: 'history-component',
  templateUrl: './history.component.html',
  styleUrls: ['./history.component.css']
})
export class HistoryComponent implements OnInit {

  @Input() history: Array<object> = [
    {
      time: '12:44:30',
      text: 'Кто-то включил чайник'
    },
    {
      time: '10:53:45',
      text: 'Кто-то включил чайник'
    }
  ];

  constructor() { }

  ngOnInit(): void {
  }

}
