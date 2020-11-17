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
  speakerElement: Element;

  constructor() {
  }

  ngOnInit(): void {
    this.speakerElement = document.querySelector('.signaler-icon-speaker')
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
    this.speakerElement.classList.add('hidden');

    const type = this.sound?.type;
    if (type && type !== SignalerSoundTypeModel.NONE) {
      const audio = this.getAudio(this.sound.type);

      audio.play()
      this.speakerElement.classList.remove('hidden');
      this.intervalId = setInterval(
        () => audio.play(),
        this.sound.interval
      )
      const execTimeout = (intervalId, timeout) => {
        setTimeout(
          () => {
            clearInterval(intervalId);
            this.speakerElement.classList.add('hidden');
          },
          timeout
        )
      }
      execTimeout(this.intervalId, this.sound.timeout)
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
    switch ((this.level ? this.level : 'NOTSIGNALLEVEL')) {
      case SignalerLevelModel.WARNING: {
        return 'signaler-level-warning';
      }
      case SignalerLevelModel.CRITICAL: {
        return 'signaler-level-critical';
      }
      case SignalerLevelModel.NOTSIGNALLEVEL: {
        return 'not-signal-level';
      }
      default: {
        return 'signaler-level-info';
      }
    }
  }
}
