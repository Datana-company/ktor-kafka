import {Component, Input, OnInit} from '@angular/core';
import {RecommendationModel} from "@datana-smart/recommendation-component";

@Component({
  // tslint:disable-next-line:component-selector
  selector: 'history-component',
  templateUrl: './history.component.html',
  styleUrls: ['./history.component.css']
})
export class HistoryComponent implements OnInit {

  @Input() history: Array<RecommendationModel> = [
    new RecommendationModel(
      new Date('2020-09-21T12:45:30'),
      'Кто-то включил чайник'
    ),
    new RecommendationModel(
      new Date('2020-09-21T12:45:30'),
      'Кто-то включил чайник'
    ),
  ];

  constructor() { }

  ngOnInit(): void {
  }

}
