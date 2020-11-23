import {Inject, Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable, throwError} from 'rxjs';
import {catchError} from "rxjs/operators";
import {HOST_CONFIG, HostConfig} from "../converter-widget-mock.module";
import {MockCasesModel} from "../models/mock-cases-model";

@Injectable({
  providedIn: 'root'
})
export class HostService {
  private readonly base: string;

  constructor(
    private http: HttpClient,
    @Inject(HOST_CONFIG) private hostConfig: HostConfig
  ) {
    this.base = hostConfig.baseUrl;
  }

  // getSelectedCaseData(): Observable<ConverterMeltInfo> {
  //     return this.http.get<ConverterMeltInfo>('/selected_case')
  // }

  addCase(formData: FormData): Observable<any> {
    console.log(` +++ POST /add_case --- formData = ${formData}`);
    // const headers = new HttpHeaders({'Content-Type': 'text/html; charset=utf-8'});
    return this.http.post(`${this.base}/add_case`, formData, {
      reportProgress: true,
      observe: 'events'
    })
      .pipe(catchError(err => this.handleError(err)));
  }

  getSelectedCaseData(selectedCaseName): Observable<any> {
    console.log(` +++ GET /get_case_data --- selectedCaseName = ${selectedCaseName}`);
    const headers = new HttpHeaders({'Content-Type': 'text/html; charset=utf-8'});
    return this.http.get(`${this.base}/get_case_data/${selectedCaseName}`, {headers})
      .pipe(catchError(err => this.handleError(err)));
  }

  getList(): Observable<MockCasesModel> {
    return this.http.get<MockCasesModel>(`${this.base}/list`);
  }

  startCase(dir: string): Observable<any> {
    console.log(`send dir = ${dir}`);
    return this.http.get<any>(`${this.base}/send`, {
      params: {
        case: dir
      }
    });
  }


  private handleError(error: any): Observable<never> {
    return throwError(error);
  }
}
