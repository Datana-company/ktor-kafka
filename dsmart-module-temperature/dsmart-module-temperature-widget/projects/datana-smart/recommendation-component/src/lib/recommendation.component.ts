import {Component, Input, OnInit} from '@angular/core';
import {RecommendationModel} from "./recommendation-model";

@Component({
  // tslint:disable-next-line:component-selector
  selector: 'recommendation-component',
  templateUrl: './recommendation.component.html',
  styleUrls: ['./recommendation.component.css']
})
export class RecommendationComponent implements OnInit {

  @Input() recommendation: RecommendationModel;

  constructor() { }

  ngOnInit(): void {
  }

}
