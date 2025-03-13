/* tslint:disable */
/* eslint-disable */
/* Code generated by ng-openapi-gen DO NOT EDIT. */

import { HttpClient, HttpContext } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { BaseService } from '../base-service';
import { ApiConfiguration } from '../api-configuration';
import { StrictHttpResponse } from '../strict-http-response';

import { getLogo } from '../fn/logo-controller/get-logo';
import { GetLogo$Params } from '../fn/logo-controller/get-logo';

@Injectable({ providedIn: 'root' })
export class LogoControllerService extends BaseService {
  constructor(config: ApiConfiguration, http: HttpClient) {
    super(config, http);
  }

  /** Path part for operation `getLogo()` */
  static readonly GetLogoPath = '/api/logo';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `getLogo()` instead.
   *
   * This method doesn't expect any request body.
   */
  getLogo$Response(params?: GetLogo$Params, context?: HttpContext): Observable<StrictHttpResponse<Blob>> {
    return getLogo(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `getLogo$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  getLogo(params?: GetLogo$Params, context?: HttpContext): Observable<Blob> {
    return this.getLogo$Response(params, context).pipe(
      map((r: StrictHttpResponse<Blob>): Blob => r.body)
    );
  }

}
