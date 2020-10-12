import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'datana-converter-widget',
  templateUrl: './converter-widget.component.html',
  styles: [
  ]
})
export class ConverterWidgetComponent implements OnInit {

  playlist: string;

  constructor() { }

  ngOnInit(): void {
    this.playlist = "http://camera.d.datana.ru/playlist.m3u8"
  }

}
