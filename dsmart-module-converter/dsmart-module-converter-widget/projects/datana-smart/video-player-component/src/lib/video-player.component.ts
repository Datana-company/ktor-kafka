import { Component, ElementRef, Input, OnDestroy, OnInit, ViewChild, ViewEncapsulation } from '@angular/core';
import videojs from 'video.js';

@Component({
  selector: 'video-player-component',
  templateUrl: './video-player.component.html',
  styleUrls: ['./video-player.component.sass'],
  encapsulation: ViewEncapsulation.None
})
export class VideoPlayerComponent implements OnInit, OnDestroy {
  @ViewChild('target', {static: true}) target: ElementRef;
  // @Input() options: {
  //   controls: boolean,
  //   preload: string,
  //   autoplay: boolean,
  //   liveui: string,
  //   html5: {
  //     vhs: {
  //       overrideNative: boolean
  //     },
  //     nativeAudioTracks: boolean,
  //     nativeVideoTracks: boolean
  //   },
  //   sources: {
  //     src: string,
  //     type: string,
  //   }[],
  // };
  player: videojs.Player;

  constructor(
    private elementRef: ElementRef,
  ) { }

  ngOnInit(): void {
    this.player = videojs(this.target.nativeElement, { autoplay: false, controls: true, preload: 'auto', liveui: true, html5: { vhs: { overrideNative: true }, nativeAudioTracks: false, nativeVideoTracks: false}, sources: [{ src: 'http://camera.d.datana.ru/playlist.m3u8', type: 'application/x-mpegURL', }]}, function onPlayerReady() {
      console.log('onPlayerReady', this);
    });
    // this.player = videojs(this.target.nativeElement, this.options, function onPlayerReady() {
    //   console.log('onPlayerReady', this);
    // });
  }

  ngOnDestroy() {
    // destroy player
    if (this.player) {
      this.player.dispose();
    }
  }

}
