import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable, throwError} from 'rxjs';
import {catchError} from "rxjs/operators";

@Injectable({
    providedIn: 'root'
})
export class CaseEditorService {

    constructor(private http: HttpClient) {
    }

    // getSelectedCaseData(): Observable<ConverterMeltInfo> {
    //     return this.http.get<ConverterMeltInfo>('/selected_case')
    // }

    addCase(formData: FormData): Observable<any> {
        console.log(` +++ POST /add_case --- formData = ${formData}`)
        const headers = new HttpHeaders({'Content-Type': 'text/html; charset=utf-8'})
        return this.http.post("/add_case", formData, {headers})
            .pipe(catchError(err => this.handleError(err)));
    }

    getSelectedCaseData(selectedCaseName): Observable<any> {
        console.log(` +++ GET /get_case_data --- selectedCaseName = ${selectedCaseName}`)
        const headers = new HttpHeaders({'Content-Type': 'text/html; charset=utf-8'})
        return this.http.get(`/get_case_data/${selectedCaseName}`, {headers})
            .pipe(catchError(err => this.handleError(err)));
    }

    private handleError(error: any) {
        return throwError(error);
    }
}
