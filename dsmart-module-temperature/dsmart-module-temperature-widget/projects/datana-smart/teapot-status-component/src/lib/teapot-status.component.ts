import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'teapot-status-component',
  templateUrl: './teapot-status.component.html',
  styleUrls: ['./teapot-status.component.css']
})
export class TeapotStatusComponent implements OnInit {

  @Input() status: boolean = false

  constructor() { }

  ngOnInit(): void {
  }

}
