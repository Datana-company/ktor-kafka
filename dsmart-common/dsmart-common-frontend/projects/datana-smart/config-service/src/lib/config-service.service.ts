import {Inject, Injectable} from '@angular/core';
import {configServiceConfig} from './config-service.config';
import {ConfigServiceConfig} from './config-service.module';
import {Observable, of} from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import {HttpClient} from '@angular/common/http';

@Injectable({providedIn: 'root'})
export class ConfigServiceService {
  private readonly restWsUrl;
  private configSettings: any = null;
  public serviceId = Math.floor(Math.random() * 1000000);

  constructor(
    @Inject(configServiceConfig) private csConfig: ConfigServiceConfig,
    private http: HttpClient
  ) {
    console.log('starting ConfigServiceService', this.serviceId, csConfig)
    this.restWsUrl = csConfig.restWsUrl;
  }

  load(): Promise<any> {
    console.log('Requesting to server', this.restWsUrl);
    return this.getAPI(this.restWsUrl)
      .pipe(map(response => {
          console.log('Response from the server /front-config :::', this.serviceId, response);
          this.configSettings = response;
        return response;
      }))
      .toPromise()
  }

  get settings() {
    return this.configSettings;
  }

  private getAPI(url: string): Observable<any> {
    const headers = new Headers({'Content-Type': 'application/json'});
    return this.http.get(url)
      .pipe(map((response: Response) => response),
        catchError(this.handleError));
  }

  private handleError(error: Response) {
    console.log('ERROR::STATUS:::::' + error.status);
    console.log('ERROR::STATUS TEXT:::::' + error.statusText);
    if (error.status === 504 || error.status === 502 || error.status === 503) {
      return of('');
    } else {
      return of(JSON.stringify(error) || 'Server error');
    }
  }

}

