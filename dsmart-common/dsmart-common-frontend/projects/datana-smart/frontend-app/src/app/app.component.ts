import {Component, OnInit} from '@angular/core';
import {ConfigServiceService} from "@datana-smart/config-service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.sass']
})
export class AppComponent  implements OnInit {
  constructor(private appConfigService: ConfigServiceService) {}
  settings: any;

  ngOnInit() {
    this.settings = this.appConfigService.settings;
    console.log("this.settings from config-service : " , this.settings)
  }
}
