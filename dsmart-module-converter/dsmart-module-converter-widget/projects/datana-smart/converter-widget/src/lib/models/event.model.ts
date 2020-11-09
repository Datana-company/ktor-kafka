import {EventCategoryModel} from "./event-category.model";
import {ExecutionStatusModel} from "./event-execution-status.model";

export class EventModel {
  constructor(
    public id: string,
    public dateStart: Date,
    public dateFinish: Date,
    public title: string,
    public textMessage: string,
    public category: EventCategoryModel,
    public isActive: boolean,
    public executionStatus: ExecutionStatusModel
  ) {
  }
}
