import { from, Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { AxiosResponse } from 'axios';

/**
 * Base API service providing common functionality for API services.
 * Includes method to wrap promises into observables.
 *
 */
export abstract class BaseApiService {

  /**
   * Wraps a promise returning an AxiosResponse into an Observable emitting the response data.
   * @param promise Promise returning AxiosResponse.
   */
  protected wrapPromise<T>(promise: Promise<AxiosResponse<T>>): Observable<T> {
    return from(promise).pipe(map((res) => res.data));
  }
}
