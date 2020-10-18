import {ModuleWithProviders, NgModule} from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {WebSocketConfig, config} from '@datana-smart/websocket';

const routes: Routes = [
  {
    path: 'converter',
    loadChildren: () => import('@datana-smart/converter-widget').then(m => m.ConverterWidgetModule)
  },
  {
    path: '**',
    redirectTo: 'converter',
    pathMatch: 'full'
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { useHash: true })],
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
