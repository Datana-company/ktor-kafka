import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {EventRecommendationComponent} from "./event-recommendation.component";

@NgModule({
  declarations: [EventRecommendationComponent],
  imports: [
    CommonModule
  ],
  exports: [EventRecommendationComponent]
})
export class EventRecommendationModule {
}
