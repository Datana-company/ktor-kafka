import {NgModule, APP_INITIALIZER, SkipSelf, Optional, ModuleWithProviders, InjectionToken} from '@angular/core';
import { HttpClientModule } from '@angular/common/http';

import {ConfigServiceService} from './config-service.service';

export function init_app(configService: ConfigServiceService) {
  return () => configService.load();
}

interface ConfigServiceConfig {
  restWsUrl: string;
}
export const USER_REST_WS_URL = new InjectionToken<ConfigServiceConfig>('unique.string.for.config');


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
        'ConfigServiceModule is already loaded. Import it in the AppModule only');
    }
  }

  /**
   * Метод который будет вызван когда модуль импортиться в root модуле
   * @param config - конфигурация, в данном случае задается имя пользователя
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


