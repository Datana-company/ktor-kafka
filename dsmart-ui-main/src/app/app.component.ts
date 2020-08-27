import {Component} from '@angular/core';
import {WebsocketService} from "./websocket";
import {Observable} from "rxjs";
import {WS} from "./websocket.events";

export interface IMessage {
    id: number;
    text: string;
}

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css']
})
export class AppComponent {
    title = 'dsmart-ui-main';

    messages$: Observable<IMessage[]>;
    counter$: Observable<number>;
    texts$: Observable<string>;

    value: string = '50';

    constructor(private wsService: WebsocketService) {
    }

    ngOnInit() {
        // this.form = this.fb.group({
        //     text: [null, [
        //         Validators.required
        //     ]]
        // });

        // get messages
        this.messages$ = this.wsService.on<IMessage[]>(WS.ON.MESSAGES);

        // get counter
        this.counter$ = this.wsService.on<number>(WS.ON.COUNTER);

        // get texts
        this.texts$ = this.wsService.on<string>(WS.ON.UPDATE_TEXTS);
    }

    public sendText(): void {
        this.wsService.send(WS.SEND.SEND_TEXT, "Hi From Client");
    }

    public removeText(index: number): void {
        this.wsService.send(WS.SEND.REMOVE_TEXT, index);
    }

}
