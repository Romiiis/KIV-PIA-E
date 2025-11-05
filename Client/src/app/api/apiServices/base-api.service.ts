import { from, Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { AxiosResponse } from 'axios';

export abstract class BaseApiService {
  protected wrapPromise<T>(promise: Promise<AxiosResponse<T>>): Observable<T> {
    return from(promise).pipe(map((res) => res.data));
  }
}
