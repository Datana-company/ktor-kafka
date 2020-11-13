import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {SignalerLevelModel} from "../models/signaler-level.model";
import {SignalerSoundModel} from "../models/signaler-sound.model";
import {SignalerSoundTypeModel} from "../models/signaler-sound-type.model";

@Component({
  selector: 'signaler',
  templateUrl: './signaler.component.html',
  styleUrls: ['./signaler.component.css']
})
export class SignalerComponent implements OnInit, OnChanges {

  @Input() level: SignalerLevelModel;
  @Input() sound: SignalerSoundModel;
  intervalId: number;

  constructor() {
  }

  ngOnInit(): void {
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (
      changes?.sound?.currentValue
      && changes?.sound?.currentValue?.type !== changes.sound.previousValue?.type
      && changes?.sound?.currentValue?.interval !== changes.sound.previousValue?.interval
    ) {
      this.handleSound();
    }
  }

  handleSound = () => {
    clearInterval(this.intervalId)

    if (this.sound?.type !== SignalerSoundTypeModel.NONE) {
      const audio = this.getAudio(this.sound.type);

      audio.play()
      this.intervalId = setInterval(
        () => audio.play(),
        this.sound.interval
      )
    }
  }

  getAudio = (soundType: SignalerSoundTypeModel) => {
    switch (soundType) {
      case SignalerSoundTypeModel.SOUND_1:
        return new Audio('/assets/sounds/sound-1.wav');
      case SignalerSoundTypeModel.SOUND_2:
        return new Audio('/assets/sounds/sound-2.wav');
      case SignalerSoundTypeModel.SOUND_3:
        return new Audio('/assets/sounds/sound-3.wav');
    }
  }

  getSignalerClass() {
    switch (this.level) {
      case SignalerLevelModel.WARNING: {
        return 'signaler-level-warning';
      }
      case SignalerLevelModel.CRITICAL: {
        return 'signaler-level-critical';
      }
      default: {
        return 'signaler-level-info';
      }
    }
  }
}
