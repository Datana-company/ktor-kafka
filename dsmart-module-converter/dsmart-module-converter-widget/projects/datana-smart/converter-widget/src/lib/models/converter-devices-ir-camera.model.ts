import {DeviceTypeModel} from "./device-type.model";
import {ConverterDevice} from "./converter-device";

export class ConverterDevicesIrCameraModel implements ConverterDevice {
  id: string;
  name: number;
  uri: string;
  deviceType: string;
  type: DeviceTypeModel;

  constructor(
    id: string,
    name: number,
    uri: string,
    deviceType: string,
    type: DeviceTypeModel
  ) {
    this.id = id;
    this.name = name;
    this.uri = uri;
    this.deviceType = deviceType;
    this.type = type;
  }
}
