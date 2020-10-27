import {NgModule, APP_INITIALIZER, SkipSelf, Optional, ModuleWithProviders, InjectionToken} from '@angular/core';
import { HttpClientModule } from '@angular/common/http';

import {ConfigServiceService} from './config-service.service';
import {USER_REST_WS_URL} from "./config-service.config";

export function init_app(configService: ConfigServiceService) {
  return () => configService.load();
}

export interface ConfigServiceConfig {
  restWsUrl: string;
}


@NgModule({
  declarations: [],
  imports: [ HttpClientModule ],
  providers: [
    ConfigServiceService,
    { provide: APP_INITIALIZER, useFactory: init_app, deps: [ConfigServiceService], multi: true }
  ],
  exports: []
})

export class ConfigServiceModule {

  constructor (@Optional() @SkipSelf() parentModule: ConfigServiceModule) {
    if (parentModule) {
      throw new Error(
        'ConfigServiceModule 111 is already loaded. Import it in the AppModule only');
    }
  }

  /**
   * Метод который будет вызван когда модуль импортиться в root модуле
   * @param config - конфигурация, в данном случае задается имя url
   */
  static forRoot(config: ConfigServiceConfig): ModuleWithProviders<ConfigServiceModule> {
    return {
      ngModule: ConfigServiceModule,
      providers: [
        { provide: USER_REST_WS_URL, useValue: config },// <=== провайдер
        ConfigServiceService,
      ]
    };
  }
}


