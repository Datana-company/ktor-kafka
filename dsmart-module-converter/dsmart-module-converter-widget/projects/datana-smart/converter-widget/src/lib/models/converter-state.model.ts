import {ConverterMeltInfoModel} from "./converter-melt-info.model";
import {EventModel} from "./event.model";

export class ConverterStateModel {
  constructor(
    public meltInfo: ConverterMeltInfoModel,
    public events: Array<EventModel>,
    public warningPoint: number,
    public playlistUrl: string
  ) {
  }
}
