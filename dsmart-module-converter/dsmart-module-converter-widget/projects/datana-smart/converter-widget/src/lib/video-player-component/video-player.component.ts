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

  get safeResourceUrl(): SafeResourceUrl {
    const defaultImage = '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';
    const encodedImage = this.getEncodedFrame();
    return this._sanitizer.bypassSecurityTrustResourceUrl(
      'data:image/jpeg;base64, ' + (encodedImage ? encodedImage : defaultImage)
    );
  }

  constructor(
    private elementRef: ElementRef,
    private _sanitizer: DomSanitizer
  ) {
  }

  ngOnInit(): void {
    this.player = videojs(this.target.nativeElement, { autoplay: false, controls: true, preload: 'auto', liveui: true, html5: { vhs: { overrideNative: true }, nativeAudioTracks: false, nativeVideoTracks: false}, sources: [{ src: this.playlist, type: 'application/x-mpegURL', }]}, function onPlayerReady() {
      console.log('onPlayerReady', this);
    });
  }

  ngOnDestroy() {
    if (this.player) {
      this.player.dispose();
    }
  }

  setSource = (evt) => {
    this.channel = evt.target.value;

    const player = document.querySelector('.video-js');
    const imageContainer = document.querySelector('.image-container');

    if (this.channel === 'video') {
      player.classList.remove('hidden')
      imageContainer.classList.add('hidden')
    } else {
      player.classList.add('hidden')
      imageContainer.classList.remove('hidden')
    }
  }

  getEncodedFrame = () => {
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
