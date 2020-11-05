import {
  Component,
  ElementRef,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  SimpleChanges,
  ViewChild,
  ViewEncapsulation
} from '@angular/core';
import videojs from 'video.js';
import { DomSanitizer } from '@angular/platform-browser';

@Component({
  selector: 'video-player-component',
  templateUrl: './video-player.component.html',
  styleUrls: ['./video-player.component.sass'],
  encapsulation: ViewEncapsulation.None
})
export class VideoPlayerComponent implements OnInit, OnDestroy, OnChanges {
  @ViewChild('target', {static: true}) target: ElementRef;
  @Input() playlist: string;
  @Input() encodedFrameCamera: string;
  @Input() encodedFrameMath: string;

  player: videojs.Player;

  source: string = 'frames-camera';

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

  ngOnChanges(changes: SimpleChanges): void {
    this.getImgSrc();
  }

  ngOnDestroy() {
    // if (this.player) {
    //   this.player.dispose();
    // }
  }

  setSource = (evt) => {
    this.source = evt.target.value;
  }

  getImgSrc = () => {
    return this._sanitizer.bypassSecurityTrustResourceUrl('data:image/jpeg;base64, ' + this.getEncodedFrame());
  }

  getEncodedFrame = () => {
    switch (this.source) {
      case 'frames-camera':
        return this.encodedFrameCamera;
      case 'frames-math':
        return this.encodedFrameMath;
      default:
        return null;
    }
  }
}
