import {Inject, Injectable, InjectionToken} from '@angular/core';
import { BaseService } from './base.service';

@Injectable()
export class ConfigServiceService extends BaseService {

  private restWsUrl = 'http://localhost:8080/front-config';
  private configSettings: any = null;

  get settings() {
    return this.configSettings;
  }

  public load(): Promise<any> {
    return new Promise((resolve, reject) => {
      this.getAPI(this.restWsUrl).subscribe((response: any) => {
        console.log('Response from the server /front-config :::', response);
        this.configSettings = response;
        resolve(true);
      });
    });
  }
}
// interface ConfigServiceConfig {
//   restWsUrl: string;
//
// }
// export const USER_REST_WS_URL = new InjectionToken<ConfigServiceConfig>('unique.string.for.config');
//

