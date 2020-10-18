// import {Component, Input, OnInit} from '@angular/core';
// import {Observable} from "rxjs";
// import {map} from "rxjs/operators";
// import {ConverterModel} from "../models/converter.model";
// import {ConverterMeltInfoModel} from "../models/converter-melt-info.model";
//
// @Component({
//
//   selector: 'tilt-angle-component',
//   templateUrl: './tilt-angle.component.html',
//   styleUrls: ['./tilt-angle.component.css']
// })
// export class TiltAngleComponent implements OnInit {
//
//   /**
//    * Текущий угол наклона конвертера
//    */
//   angleConverterStream$: Observable<number>;
//
//   constructor() {
//   }
//
//   ngOnInit(): void {
//   }
//
//   // this.angleConverterStream$ = this.converterStream$.pipe(
//   //   map(({angle}) => angle)
//   // );
//   //
//   //
//   this.converterStream$ = this.wsService.on('converter-update').pipe(
//     map((data: any) => {
//       return new ConverterModel(
//         data?.angle as number
//       );
//     }));
// }
