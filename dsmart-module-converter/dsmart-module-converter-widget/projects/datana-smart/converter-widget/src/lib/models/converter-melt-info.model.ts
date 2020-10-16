import {ConverterMeltDevicesModel} from "./converter-melt-devices.model";
import {ConverterMeltModeModel} from "./converter-melt-mode.model";

export class ConverterMeltInfoModel {
  constructor(
    public id: string,
    public timeStart: number,
    public meltNumber: string,
    public steelGrade: string,
    public crewNumber: string,
    public shiftNumber: string,
    public mode: ConverterMeltModeModel,
    public devices: ConverterMeltDevicesModel
  ) {
  }
}
