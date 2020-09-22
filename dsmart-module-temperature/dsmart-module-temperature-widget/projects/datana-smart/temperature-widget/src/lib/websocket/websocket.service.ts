import {Injectable, OnDestroy, Inject} from '@angular/core';
import {Observable, SubscriptionLike, Subject, Observer, interval} from 'rxjs';
import {filter, map} from 'rxjs/operators';
import {WebSocketSubject, WebSocketSubjectConfig} from 'rxjs/webSocket';

import {share, distinctUntilChanged, takeWhile} from 'rxjs/operators';
import {IWebsocketService, IWsMessage, WebSocketConfig} from './websocket.interfaces';
import {config} from './websocket.config';


@Injectable({
  providedIn: 'root'
})
export class WebsocketService implements IWebsocketService, OnDestroy {

  private readonly config: WebSocketSubjectConfig<IWsMessage<any>>;

  private websocketSub: SubscriptionLike;
  private statusSub: SubscriptionLike;

  private reconnection$: Observable<number>;
  private websocket$: WebSocketSubject<IWsMessage<any>>;
  private connection$: Observer<boolean>;
  private wsMessages$: Subject<IWsMessage<any>>;

  private readonly reconnectInterval: number;
  private readonly reconnectAttempts: number;
  private isConnected: boolean;


  public status: Observable<boolean>;

  // constructor(@Inject(config) private wsConfig: WebSocketConfig) {
    // console.log('websocket is started', wsConfig);
    // this.wsMessages$ = new Subject<IWsMessage<any>>();
    //
    // this.reconnectInterval = wsConfig.reconnectInterval || 5000; // pause between connections
    // this.reconnectAttempts = wsConfig.reconnectAttempts || 10; // number of connection attempts
    //
    // this.config = {
    //   url: wsConfig.url,
    //   closeObserver: {
    //     next: (event: CloseEvent) => {
    //       this.websocket$ = null;
    //       this.connection$.next(false);
    //     }
    //   },
    //   openObserver: {
    //     next: (event: Event) => {
    //       console.log('WebSocket connected!');
    //       this.connection$.next(true);
    //     }
    //   }
    // };
    //
    // // connection status
    // this.status = new Observable<boolean>((observer) => {
    //   this.connection$ = observer;
    // }).pipe(share(), distinctUntilChanged());
    //
    // // run reconnect if not connection
    // this.statusSub = this.status
    //   .subscribe((isConnected) => {
    //     this.isConnected = isConnected;
    //
    //     if (!this.reconnection$ && typeof (isConnected) === 'boolean' && !isConnected) {
    //       this.reconnect();
    //     }
    //   });
    //
    // this.websocketSub = this.wsMessages$.subscribe(
    //   null, (error: ErrorEvent) => console.error('WebSocket error!', error)
    // );
    //
    // this.connect();
  // }

  ngOnDestroy(): void {
    this.websocketSub.unsubscribe();
    this.statusSub.unsubscribe();
  }


  /*
  * connect to WebSocket
  * */
  private connect(): void {
    try {
      console.log('starting connection to', this.config);
      this.websocket$ = new WebSocketSubject(this.config);

      this.websocket$.subscribe(
        (message) => {
          console.log('message: ', message);
          return this.wsMessages$.next(message);
        },
        (error: Event) => {
          if (!this.websocket$) {
            // run reconnect if errors
            this.reconnect();
          }
        },
        () => console.log('websocket connection complete'),
      );
    } catch (e) {
      // console.log(`Error connecting to Websocket ${this.wsConfig.url}`);
    }
  }


  /*
  * reconnect if not connecting or errors
  * */
  private reconnect(): void {
    console.log('reconnect');
    this.reconnection$ = interval(this.reconnectInterval)
      .pipe(takeWhile((v, index) => index < this.reconnectAttempts && !this.websocket$));

    this.reconnection$.subscribe(
      () => this.connect(),
      null,
      () => {
        // Subject complete if reconnect attemts ending
        this.reconnection$ = null;

        if (!this.websocket$) {
          this.wsMessages$.complete();
          this.connection$.complete();
        }
      });
  }


  /*
  * on message event
  * */
  public on<T>(event: string): Observable<T> {
    if (event) {
      return this.wsMessages$.pipe(
        map((message) => {
          console.log(message);
          return message as IWsMessage<T>;
        }),
        filter((message: IWsMessage<T>) => {
          console.log('Check for types', event, message.event);
          return message.event === event;
        }),
        map((message: IWsMessage<T>) => {
          console.log('GOT[temp]', message, message.data);
          return message.data;
        })
      );
    }
  }


  /*
  * on message to server
  * */
  public send(event: string, data: any = {}): void {
    if (event && this.isConnected) {
      this.websocket$.next(JSON.stringify({event, data}) as any);
    } else {
      console.error('Send error!');
    }
  }

}
