import { InjectionToken } from '@angular/core';
import {IWebsocketService} from './websocket.interfaces';

export const config: InjectionToken<string> = new InjectionToken('websocket.interfaces.ts.config');
export const configProvide = new InjectionToken<string>('websocket.interfaces.ts.service');
