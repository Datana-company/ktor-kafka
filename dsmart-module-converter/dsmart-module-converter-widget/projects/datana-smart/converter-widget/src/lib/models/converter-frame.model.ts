import {ConverterMeltInfoModel} from "./converter-melt-info.model";

export class ConverterFrameModel {
  constructor(
    public frameId: string,
    public frameTime: number,
    public framePath: string
  ) {
  }
}
