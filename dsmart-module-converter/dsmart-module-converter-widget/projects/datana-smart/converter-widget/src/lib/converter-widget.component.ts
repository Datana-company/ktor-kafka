import {Component, Inject, OnInit} from '@angular/core';
import {Observable} from "rxjs";
import {map} from 'rxjs/operators';
import {configProvide, IWebsocketService} from '@datana-smart/websocket';
import {RecommendationModel} from "./models/recommendation.model";
import {ConverterModel} from "./models/converter.model";
import {ConverterMeltInfoModel} from "./models/converter-melt-info.model";

@Component({
  selector: 'datana-converter-widget',
  templateUrl: './converter-widget.component.html',
  styles: [
  ]
})
export class ConverterWidgetComponent implements OnInit {

  private converterStream$: Observable<ConverterModel>;
  private recommendationStream$: Observable<RecommendationModel>;

  /**
   * Текущий угол наклона конвертера
   */
  angleConverterStream$: Observable<number>;

  /**
  * Время начала плавки
  */
  timeStartMeltStream$: Observable<number>;

  dateRecommendationStream$: Observable<Date>;

  titleRecommendationStream$: Observable<string>;

  textMessageRecommendationStream$: Observable<string>;

  playlist: string;

  constructor(
    @Inject(configProvide) private wsService: IWebsocketService
  ) { }

  ngOnInit(): void {
    this.playlist = "http://camera.d.datana.ru/playlist.m3u8"

    this.converterStream$ = this.wsService.on('converter-update').pipe(
      map((data: any) => {
        return new ConverterModel(
          data?.frameId as string,
          data?.frameTime as number,
          data?.framePath as string,
          data?.meltInfo as ConverterMeltInfoModel,
          data?.angle as number,
          data?.steelRate as number,
          data?.slagRate as number
        );
      })
    );

    this.recommendationStream$ = this.wsService.on('recommendation-update').pipe(
      map((data: any) => {
        return new RecommendationModel(
          new Date(data?.time as number),
          data?.title as string,
          data?.textMessage as string
        );
      })
    );

    this.angleConverterStream$ = this.converterStream$.pipe(
      map(({angle}) => angle)
    );

    this.timeStartMeltStream$ = this.converterStream$.pipe(
      map(({meltInfo}) => meltInfo?.timeStart as number)
    );

    this.dateRecommendationStream$ = this.recommendationStream$.pipe(
      map(({date}) => date)
    );

    this.titleRecommendationStream$ = this.recommendationStream$.pipe(
      map(({title}) => title)
    );

    this.textMessageRecommendationStream$ = this.recommendationStream$.pipe(
      map(({textMessage}) => textMessage)
    );
  }

}
