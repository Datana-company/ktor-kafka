import { InjectionToken } from '@angular/core';

export const config: InjectionToken<string> = new InjectionToken('websocket.config');
export const configProvide = new InjectionToken<string>('websocket.service');
