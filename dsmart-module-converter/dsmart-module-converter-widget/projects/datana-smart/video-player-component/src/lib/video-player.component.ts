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
  @Input() playlist: string;

  player: videojs.Player;

  constructor(
    private elementRef: ElementRef,
  ) { }

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

}
