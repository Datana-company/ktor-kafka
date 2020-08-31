import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-sensor-data',
  templateUrl: './sensor-data.component.html',
  styleUrls: ['./sensor-data.component.css']
})
export class SensorDataComponent implements OnInit {

  value = '50';

  constructor() { }

  ngOnInit(): void {
  }

}
