// core/api/query.utils.ts
import { inject } from '@angular/core';
import { ApiResult } from './apiServices/base-api.service';
import { AuthService } from '@core/auth/auth.service';

export async function queryFromApi<T>(promise: Promise<ApiResult<T>>): Promise<T> {
  let res = await promise;
  if (!res.ok) throw new Error(res.error ?? `Request failed (${res.status})`);
  return res.data as T;
}
