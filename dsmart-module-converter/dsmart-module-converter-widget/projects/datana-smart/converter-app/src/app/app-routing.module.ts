import {APP_INITIALIZER, ModuleWithProviders, NgModule} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';
import {WebSocketConfig, config} from '@datana-smart/websocket';
import {ConfigServiceConfig, configServiceConfig, ConfigServiceService} from '@datana-smart/config-service';
import {HttpClient, HttpClientModule} from '@angular/common/http';


const routes: Routes = [
  {
    path: 'converter',
    loadChildren: () => import('@datana-smart/converter-widget').then(m => {
      console.log('loading ConverterWidgetModule', m);
      return m.ConverterWidgetModule;
    })
  },
  {
    path: '**',
    redirectTo: 'converter',
    pathMatch: 'full'
  }
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, {useHash: true}),
    HttpClientModule,
  ],
  exports: [RouterModule]
})
export class AppRoutingModule {
  public static config(wsConfig: WebSocketConfig, csConfig: ConfigServiceConfig ): ModuleWithProviders<AppRoutingModule> {
    console.log('Setting up AppRoutingModule', wsConfig, csConfig);
    return {
      ngModule: AppRoutingModule,
      providers: [
        HttpClient,
        {provide: config, useValue: wsConfig},
        {provide: configServiceConfig, useValue: csConfig},
        {
          provide: APP_INITIALIZER,
          useFactory: (service: ConfigServiceService) => () => {
            console.log('Requesting for settings');
            return service.load();
          },
          deps: [ConfigServiceService, configServiceConfig, HttpClient],
          multi: true
        }
      ]
    };
  }
}
