import {DeviceTypeModel} from "./device-type.model";

export interface ConverterDevice {
    id: string,
    name: number,
    uri: string,
    deviceType: string,
    type: DeviceTypeModel
}
