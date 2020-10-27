import {InjectionToken} from "@angular/core";
import {ConfigServiceConfig} from "./config-service.module";

export const USER_REST_WS_URL = new InjectionToken<ConfigServiceConfig>('unique.string.for.config');
