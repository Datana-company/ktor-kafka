import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from 'rxjs';
import {MockCasesModel} from "./models/mock-cases-model";
import {Router} from "@angular/router";

@Injectable({
  providedIn: 'root'
})
export class ConverterWidgetMockService {

  constructor(private http: HttpClient) {
  }

  getList(): Observable<MockCasesModel> {
    return this.http.get<MockCasesModel>('/list');
  }

  startCase(dir: string): Observable<any> {
    console.log(`send dir = ${dir}`);
    return this.http.get<any>('/send', {
      params: {
        case: dir
      }
    });
  }
}
