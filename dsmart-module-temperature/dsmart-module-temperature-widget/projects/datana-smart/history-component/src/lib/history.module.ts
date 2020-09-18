import { NgModule } from '@angular/core';
import { HistoryComponent } from './history.component';
import { RecommendationModule } from '@datana-smart/recommendation-component';


@NgModule({
  declarations: [HistoryComponent],
  imports: [
    RecommendationModule
  ],
  exports: [HistoryComponent]
})
export class HistoryModule { }
