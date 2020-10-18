import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from 'rxjs';
import {MockCasesModel} from "./models/mock-cases-model";

@Injectable({
  providedIn: 'root'
})
export class ConverterWidgetMockService {

  constructor(private http: HttpClient) {
  }

  getList(): Observable<MockCasesModel> {
    return this.http.get<MockCasesModel>("http://localhost:8080/list")
  }

  startCase(dir: string): Observable<any> {
    console.log(`send dir = ${dir}`)
    return this.http.get<any>("http://localhost:8080/send", {
      params: {
        case: dir
      }
    })
  }
}
