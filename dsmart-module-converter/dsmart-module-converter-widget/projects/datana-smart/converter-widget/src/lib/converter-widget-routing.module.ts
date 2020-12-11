import {APP_INITIALIZER, ModuleWithProviders, NgModule} from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {ConverterWidgetComponent} from './converter-widget.component';
// import {configServiceConfig, ConfigServiceService} from '@datana-smart/config-service';
// import {HttpClient} from '@angular/common/http';

const routes: Routes = [
  {
    path: '',
    component: ConverterWidgetComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ConverterWidgetRoutingModule {
  // public static config(): ModuleWithProviders<ConverterWidgetRoutingModule> {
  //   console.log('Setting up ConverterWidgetRoutingModule');
  //   return {
  //     ngModule: ConverterWidgetRoutingModule,
  //     providers: [
  //       HttpClient,
  //       {
  //         provide: APP_INITIALIZER,
  //         useFactory: (service: ConfigServiceService) => () => {
  //           console.log('Requesting for settings');
  //           return service.load();
  //         },
  //         deps: [ConfigServiceService, configServiceConfig, HttpClient],
  //         multi: true
  //       }
  //     ]
  //   };
  // }
}
