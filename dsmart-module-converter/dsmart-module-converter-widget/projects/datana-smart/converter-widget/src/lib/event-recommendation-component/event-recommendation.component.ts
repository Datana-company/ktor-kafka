import {Component, Inject, Input, OnInit} from '@angular/core';
import {TemperatureModel} from "../models/temperature.model";
import {EventModel} from "../models/event-model";

@Component({
  selector: 'event-recommendation-component',
  templateUrl: './event-recommendation-component.component.html',
  styleUrls: ['./event-recommendation-component.component.css']
})
export class EventRecommendationComponent  {
  // @Input() events: EventModel;
  @Input() events:  Array<EventModel> = new Array<EventModel>();
}
