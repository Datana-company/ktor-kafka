import { Component } from '@angular/core';
import {RecommendationModel} from "@datana-smart/recommendation-component";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.sass']
})
export class AppComponent {
  title = 'temperature-app';
  time = '12:34:56';
  recommendation: "some recommendation";
  x: RecommendationModel;
}
