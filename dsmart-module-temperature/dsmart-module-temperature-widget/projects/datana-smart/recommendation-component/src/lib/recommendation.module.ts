import { NgModule } from '@angular/core';
import { RecommendationComponent } from './recommendation.component';
import {CommonModule} from "@angular/common";



@NgModule({
  declarations: [RecommendationComponent],
  imports: [
    CommonModule
  ],
  exports: [
    RecommendationComponent
  ]
})
export class RecommendationModule { }
