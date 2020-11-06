import {
  Component,
  ElementRef,
  Input,
  OnDestroy,
  OnInit,
  ViewChild,
  ViewEncapsulation
} from '@angular/core';
import videojs from 'video.js';
import {DomSanitizer, SafeResourceUrl} from '@angular/platform-browser';

@Component({
  selector: 'video-player-component',
  templateUrl: './video-player.component.html',
  styleUrls: ['./video-player.component.sass'],
  encapsulation: ViewEncapsulation.None
})
export class VideoPlayerComponent implements OnInit, OnDestroy {
  @ViewChild('target', {static: true}) target: ElementRef;
  @Input() playlist: string;
  @Input() imageCamera: string;
  @Input() imageMath: string;

  player: videojs.Player;

  channel: string = 'camera';

  _safeResourceUrl: SafeResourceUrl
  // _defaultImage = 'iVBORw0KGgoAAAANSUhEUgAAAcIAAAEsCAQAAAD6X3s8AAACeElEQVR42u3TsQ0AAAjDMPr/0fQGJhb7hEhJdoBHMSGYEEwImBBMCJgQTAiYEEwImBBMCJgQTAiYEEwImBBMCJgQTAiYEEwImBBMCJgQTAiYEEwImBBMCJgQTAiYEEwImBBMCJgQTAiYEEwImBBMCJgQTAiYEEwImBBMCJgQTAiYEEwImBBMCJgQTAiYEEwImBBMCJgQTAiYEEwImBBMCCY0IZgQTAiYEEwImBBMCJgQTAiYEEwImBBMCJgQTAiYEEwImBBMCJgQTAiYEEwImBBMCJgQTAiYEEwImBBMCJgQTAiYEEwImBBMCJgQTAiYEEwImBBMCJgQTAiYEEwImBBMCJgQTAiYEEwImBBMCJgQTAiYEEwImBBMCJgQTAiYEEwIJhQBTAgmBEwIJgRMCCYETAgmBEwIJgRMCCYETAgmBEwIJgRMCCYETAgmBEwIJgRMCCYETAgmBEwIJgRMCCYETAgmBEwIJgRMCCYETAgmBEwIJgRMCCYETAgmBEwIJgRMCCYETAgmBEwIJgRMCCYETAgmBEwIJgRMCCYETAgmBEwIJgQTAiYEEwImBBMCJgQTAiYEEwImBBMCJgQTAiYEEwImBBMCJgQTAiYEEwImBBMCJgQTAiYEEwImBBMCJgQTAiYEEwImBBMCJgQTAiYEEwImBBMCJgQTAiYEEwImBBMCJgQTAiYEEwImBBMCJgQTAiYEEwImBBMCJgQTAiYEE4IJTQgmBBMCJgQTAiYEEwImBBMCJgQTAiYEEwImBBMCJgQTAiYEEwImBBMCJgQTAiYEEwImBBMCJgQTAiYEEwImBBMCJgQTAiYEEwImBBMCJgQTAjcF32EtPOcUf28AAAAASUVORK5CYII=';

  get safeResourceUrl(): SafeResourceUrl {
    console.log('------- getting safeResourceUrl')
    return this._sanitizer.bypassSecurityTrustResourceUrl(
      'data:image/jpeg;base64, ' + this.getEncodedFrame()
    );
  }

  constructor(
    private elementRef: ElementRef,
    private _sanitizer: DomSanitizer
  ) {
  }

  ngOnInit(): void {
    // this.player = videojs(this.target.nativeElement, { autoplay: false, controls: true, preload: 'auto', liveui: true, html5: { vhs: { overrideNative: true }, nativeAudioTracks: false, nativeVideoTracks: false}, sources: [{ src: this.playlist, type: 'application/x-mpegURL', }]}, function onPlayerReady() {
    //   console.log('onPlayerReady', this);
    // });
  }

  ngOnDestroy() {
    // if (this.player) {
    //   this.player.dispose();
    // }
  }

  setSource = (evt) => {
    console.log('------- changing source')
    this.channel = evt.target.value;
  }

  getEncodedFrame = () => {
    console.log('------- getting encodedFrame')
    switch (this.channel) {
      case 'camera':
        return this.imageCamera;
      case 'math':
        return this.imageMath;
      default:
        return null;
    }
  }
}
