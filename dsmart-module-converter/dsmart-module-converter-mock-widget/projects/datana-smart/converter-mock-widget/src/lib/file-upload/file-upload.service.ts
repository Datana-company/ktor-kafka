import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';

import {catchError} from 'rxjs/operators';

@Injectable({
    providedIn: 'root'
})
export class FileUploadService {

    constructor(private http: HttpClient) {
    }

    uploadWithProgress(formData: FormData): Observable<any> {
        return this.http.post("/upload", formData, {observe: 'events', reportProgress: true})
            .pipe(
                catchError(err => this.handleError(err))
            );
    }

    private handleError(error: any) {
        return throwError(error);
    }
}
