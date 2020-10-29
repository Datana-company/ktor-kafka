import {ConverterDevicesIrCameraModel} from "./converter-devices-ir-camera.model";
import {ConverterDevicesConverterModel} from "./converter-devices-converter.model";
import {ConverterDevicesSelsynModel} from "./converter-devices-selsyn.model";
import {ConverterDevicesSlagRateModel} from "./converter-devices-slag-rate";

export class ConverterMeltDevicesModel {
  constructor(
    public converter: ConverterDevicesConverterModel,
    public irCamera: ConverterDevicesIrCameraModel,
    public selsyn: ConverterDevicesSelsynModel,
    public slagRate: ConverterDevicesSlagRateModel
  ) {
  }
}
