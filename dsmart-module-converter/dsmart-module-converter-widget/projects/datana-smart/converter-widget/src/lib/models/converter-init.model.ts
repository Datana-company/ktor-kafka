import {ConverterMeltInfoModel} from "./converter-melt-info.model";
import {EventModel} from "./event.model";

export class ConverterInitModel {
  constructor(
    public meltInfo: ConverterMeltInfoModel,
    public events: Array<EventModel>
  ) {
  }
}
