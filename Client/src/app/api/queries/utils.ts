// src/app/api/queries/utils.ts
import { firstValueFrom } from 'rxjs';

export const toPromise = <T>(obs: import('rxjs').Observable<T>): Promise<T> =>
  firstValueFrom(obs);
