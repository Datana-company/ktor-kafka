import {Component, Input, OnInit} from '@angular/core';
import {RecommendationModel} from "./recommendation-model";

@Component({
  // tslint:disable-next-line:component-selector
  selector: 'recommendation-component',
  templateUrl: './recommendation.component.html',
  styleUrls: ['./recommendation.component.css']
})
export class RecommendationComponent implements OnInit {

  @Input() recommendation: RecommendationModel = new RecommendationModel(
    new Date('2020-09-21T12:45:30'),
    'Внимание чайник закипит через 2 минут. Готовьтесь заваривать чай!'
  );

  constructor() { }

  ngOnInit(): void {
  }

}
