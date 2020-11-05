import {Component, Inject, Input, OnInit} from '@angular/core';
import {EventModel} from "../models/event.model";
import {ExecutionStatusModel} from "../models/event-execution-status.model";
import {EventCategoryModel} from "../models/event-category.model";

@Component({
  selector: 'event-recommendation-component',
  templateUrl: './event-recommendation.component.html',
  styleUrls: ['./event-recommendation.component.css']
})
export class EventRecommendationComponent  {
  @Input() events:  Array<EventModel> = new Array<EventModel>();

  public isCompleted(executionStatus: ExecutionStatusModel) {
    return executionStatus == ExecutionStatusModel.COMPLETED;
  }

  public isFailed(executionStatus: ExecutionStatusModel) {
    return executionStatus == ExecutionStatusModel.FAILED;
  }

  public isWarning(eventCategoryModel: EventCategoryModel) {
    return eventCategoryModel == EventCategoryModel.CRITICAL || eventCategoryModel == EventCategoryModel.WARNING;
  }

  public isInfo(eventCategoryModel: EventCategoryModel) {
    return eventCategoryModel == EventCategoryModel.INFO || eventCategoryModel == EventCategoryModel.HINT;
  }
}
