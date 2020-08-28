import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'datana-temperature-view',
  templateUrl: './temperature-view.component.html',
  styleUrls: ['./temperature-view.component.css']
})
export class TemperatureViewComponent implements OnInit {

  @Input() value: string = '0';

  constructor() { }

  ngOnInit(): void {
  }

}
