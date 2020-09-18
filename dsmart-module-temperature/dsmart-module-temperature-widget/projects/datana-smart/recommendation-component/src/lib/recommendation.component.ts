import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'recommendation-component',
  templateUrl: './recommendation.component.html',
  styleUrls: ['./recommendation.component.css']
})
export class RecommendationComponent implements OnInit {

  @Input() time: string = '12:45:30'

  @Input() recommendation: string = ' Чайник закипит через 2 минуты. Приготовьтесь наливать чай.'

  constructor() { }

  ngOnInit(): void {
  }

}
