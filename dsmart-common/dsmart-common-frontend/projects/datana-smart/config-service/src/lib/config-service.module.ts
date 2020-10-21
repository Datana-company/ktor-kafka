// import { NgModule, APP_INITIALIZER } from '@angular/core';
// import { HttpClientModule } from '@angular/common/http';
// import { ConfigService } from './config-service.service';
//
//
// @NgModule({
//   declarations: [],
//   imports: [
//   ],
//   exports: []
// })
// export class ConfigServiceModule { }
import { NgModule, APP_INITIALIZER } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';

import { ConfigServiceService } from './index';

export function init_app(appConfigService: ConfigServiceService) {
  return () => appConfigService.load();
}

@NgModule({
  imports: [ HttpClientModule ],
  providers: [
    ConfigServiceService,
    { provide: APP_INITIALIZER, useFactory: init_app, deps: [ConfigServiceService], multi: true }
  ]
})
export class ConfigServiceServiceModule { }

