import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable, throwError} from 'rxjs';
import {catchError, retry} from 'rxjs/operators';
import {MockListItemModel} from "./models/mock-list-item-model";

@Injectable({
  providedIn: 'root'
})
export class ConverterWidgetMockService {

  constructor(private http: HttpClient) {
  }

  async getList() {
    this.http.get<MockListItemModel>("/list")
  }

  startCase(dir: string) {
    this.http.get(`/send`, {
      params: {
        case: dir
      }
    })
  }
}
