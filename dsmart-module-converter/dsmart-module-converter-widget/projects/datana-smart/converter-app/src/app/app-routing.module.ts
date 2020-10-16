import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

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
export class AppRoutingModule { }
