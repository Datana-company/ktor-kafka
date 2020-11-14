import {SignalerSoundTypeModel} from "./signaler-sound-type.model";

export class SignalerSoundModel {

  constructor(
    public type: SignalerSoundTypeModel,
    public interval: number,
    public timeout: number
  ) {
  }

}
