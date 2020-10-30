import {NgModule, ModuleWithProviders} from '@angular/core';
import {HttpClient, HttpClientModule} from '@angular/common/http';

import {ConfigServiceService} from './config-service.service';
import {configServiceConfig, configServiceProvide} from './config-service.config';
import {CommonModule} from '@angular/common';

export interface ConfigServiceConfig {
  restWsUrl: string;
}

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    HttpClientModule,
  ],
  providers: [
    HttpClient,
    ConfigServiceService,
    {provide: configServiceProvide, useClass: ConfigServiceService}
  ],
  exports: []
})

export class ConfigServiceModule {
  public static config(csConfig: ConfigServiceConfig): ModuleWithProviders<ConfigServiceModule> {
    console.log('ConfigServiceModule', csConfig)
    return {
      ngModule: ConfigServiceModule,
      providers: [{provide: configServiceConfig, useValue: csConfig}]
    };
  }
}


