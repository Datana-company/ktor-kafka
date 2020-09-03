import {Component, Inject, OnInit} from '@angular/core';

export interface IMessage {
  id: number;
  text: string;
}

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'dsmart-ui-main';

  constructor(
  ) {
  }

  ngOnInit(): void {
  }

}
