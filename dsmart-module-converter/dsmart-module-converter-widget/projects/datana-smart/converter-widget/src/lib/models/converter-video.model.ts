import {ConverterMeltInfoModel} from "./converter-melt-info.model";

export class ConverterVideoModel {
  constructor(
    public frameId: string,
    public frameTime: number,
    public framePath: string,
    public meltInfo: ConverterMeltInfoModel
  ) {
  }
}
