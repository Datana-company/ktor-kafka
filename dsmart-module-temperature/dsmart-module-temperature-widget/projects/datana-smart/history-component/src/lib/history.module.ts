import { NgModule } from '@angular/core';
import { HistoryComponent } from './history.component';
import {RecommendationModule} from "../../../recommendation-component/src/lib/recommendation.module";



@NgModule({
  declarations: [HistoryComponent],
  imports: [
    RecommendationModule
  ],
  exports: [HistoryComponent]
})
export class HistoryModule { }
