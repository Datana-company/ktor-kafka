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
  @Input() irCameraId: string;
  @Input() irCameraName: string;

  // player: videojs.Player;

  // channel = 'camera';
  channel = 'math';

  _safeResourceUrl: SafeResourceUrl

  get safeResourceUrl(): SafeResourceUrl {
    const encodedImage = this.getEncodedFrame();
    return this._sanitizer.bypassSecurityTrustResourceUrl(
      'data:image/jpeg;base64, ' + encodedImage);
  }

  constructor(
    private elementRef: ElementRef,
    private _sanitizer: DomSanitizer
  ) {
  }

  ngOnInit(): void {
    // this.player = videojs(
    //   this.target.nativeElement,
    //   {
    //     autoplay: false,
    //     controls: true,
    //     preload: 'auto',
    //     liveui: true,
    //     html5: {vhs: {overrideNative: true}, nativeAudioTracks: false, nativeVideoTracks: false},
    //     sources: [{src: this.playlist, type: 'application/x-mpegURL',}]
    //   }, function onPlayerReady() {
    //     console.log('onPlayerReady', this);
    //   });
  }

  ngOnDestroy() {
    // if (this.player) {
    //   this.player.dispose();
    // }
  }

  isEncodedImageEmpty() {
    return !!this.getEncodedFrame();
  }

  setSource = (evt) => {
    this.channel = evt.target.value;

    // const player = document.querySelector('.video-js');
    const imageContainer = document.querySelector('.image-container');

    if (this.channel === 'video') {
      // player.classList.remove('hidden')
      imageContainer.classList.add('hidden')
    } else {
      // player.classList.add('hidden')
      imageContainer.classList.remove('hidden')
    }
  }
  get getCameraNameAndId() {
    const cameraName = this.irCameraName;
    return cameraName ? cameraName.toString()
          .concat(': ', this.irCameraId.toString()) : 'Тепловизор -';
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
