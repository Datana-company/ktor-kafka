import {ConverterMeltInfoModel} from "./converter-melt-info.model";

export class ConverterModel {
  constructor(
    public frameId: string,
    public frameTime: number,
    public framePath: string,
    public meltInfo: ConverterMeltInfoModel,
    public angle: number,
    public steelRate: number,
    public slagRate: number
  ) {
  }
}
