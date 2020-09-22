import {Component, Input, OnInit} from '@angular/core';
import {RecommendationModel} from '@datana-smart/recommendation-component';

@Component({
  // tslint:disable-next-line:component-selector
  selector: 'history-component',
  templateUrl: './history.component.html',
  styleUrls: ['./history.component.css']
})
export class HistoryComponent implements OnInit {

  @Input() history: Array<RecommendationModel>;

  constructor() { }

  ngOnInit(): void {
  }

}
