import { firstValueFrom } from 'rxjs';

/**
 * Converts an Observable to a Promise by taking the first emitted value.
 * @param obs Observable to convert.
 * @returns Promise resolving to the first value emitted by the Observable.
 */
export const toPromise = <T>(obs: import('rxjs').Observable<T>): Promise<T> =>
  firstValueFrom(obs);
