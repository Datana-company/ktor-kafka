import {ModuleWithProviders, NgModule} from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {WebSocketConfig, config} from '@datana-smart/websocket';

const routes: Routes = [
  {
    path: '',
    loadChildren: () => import('@datana-smart/temperature-widget').then(m => m.TemperatureViewModule)
  },
  {
    path: '',
    redirectTo: '',
    pathMatch: 'full'
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
  public static config(wsConfig: WebSocketConfig): ModuleWithProviders<AppRoutingModule> {
    console.log('Setting up AppRoutingModule', wsConfig);
    return {
      ngModule: AppRoutingModule,
      providers: [{provide: config, useValue: wsConfig}]
    };
  }
}
