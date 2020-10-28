import {Component, Inject, OnInit} from '@angular/core';
import {map, takeUntil} from "rxjs/operators";
import {EventModel} from "../models/event-model";
import {EventCategoryModel} from "../models/event-category.model";
import {Subject} from "rxjs";
import {configProvide, IWebsocketService} from "@datana-smart/websocket";

@Component({
  selector: 'event-recommendation-component',
  templateUrl: './event-recommendation-component.component.html',
  styleUrls: ['./event-recommendation-component.component.css']
})
export class EventRecommendationComponent implements OnInit {

  _unsubscribe = new Subject<void>();

  public events: Array<EventModel> = new Array<EventModel>();

  constructor(
    @Inject(configProvide) private wsService: IWebsocketService
  ) {
  }

  ngOnInit(): void {
    this.wsService.on('events-update').pipe(
      takeUntil(this._unsubscribe),
      map((data: any) => data?.list.map(
        event => new EventModel(
          event?.id as string,
          new Date(event?.timeStart as number),
          new Date(event?.timeFinish as number),
          event?.title as string,
          event?.textMessage as string,
          event?.category as EventCategoryModel,
          event?.isActive as boolean
        )
      ) as Array<EventModel>)
    ).subscribe(data => {
      this.events = data;
    });
  }

  ngOnDestroy(): void {
    this._unsubscribe.next();
    this._unsubscribe.complete();
  }

}
