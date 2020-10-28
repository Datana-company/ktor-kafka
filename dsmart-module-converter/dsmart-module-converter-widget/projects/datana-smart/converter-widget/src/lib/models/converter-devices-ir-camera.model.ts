import {DevicesIrCameraTypeModel} from "./devices-ir-camera-type.model";


export class ConverterDevicesIrCameraModel {
  constructor(
    public id: string,
    public name: string,
    public uri: string,
    public type: DevicesIrCameraTypeModel
  ) {
  }
}
