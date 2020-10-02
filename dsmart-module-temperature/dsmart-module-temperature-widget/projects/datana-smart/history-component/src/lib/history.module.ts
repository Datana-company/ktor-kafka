import { NgModule } from '@angular/core';
import { HistoryComponent } from './history.component';
import { RecommendationModule } from '@datana-smart/recommendation-component';
import {CommonModule} from "@angular/common";


@NgModule({
  declarations: [HistoryComponent],
  imports: [
    CommonModule,
    RecommendationModule
  ],
  exports: [HistoryComponent]
})
export class HistoryModule { }
