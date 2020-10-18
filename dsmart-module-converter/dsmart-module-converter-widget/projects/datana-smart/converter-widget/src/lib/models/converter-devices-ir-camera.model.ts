import {DevicesIrCamertaTypeModel} from "./devices-ir-camera-type.model";


export class ConverterDevicesIrCamertaModel {
  constructor(
    public id: string,
    public name: string,
    public uri: string,
    public type: DevicesIrCamertaTypeModel
  ) {
  }
}
