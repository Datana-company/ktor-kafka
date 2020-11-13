import {SignalerLevelModel} from "./signaler-level.model";
import {SignalerSoundModel} from "./signaler-sound.model";

export class SignalerModel {

  constructor(
    public level: SignalerLevelModel,
    public sound: SignalerSoundModel
  ) {
  }

}
