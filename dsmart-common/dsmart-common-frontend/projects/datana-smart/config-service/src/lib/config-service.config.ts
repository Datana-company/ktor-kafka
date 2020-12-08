import {InjectionToken} from '@angular/core';

export const configServiceConfig: InjectionToken<string> = new InjectionToken('config-service.config');
export const configServiceProvide = new InjectionToken<string>('config-service.service');
