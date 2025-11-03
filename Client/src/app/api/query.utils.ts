// core/api/query.utils.ts
import { ApiResult } from './apiServices/base-api.service';

export async function queryFromApi<T>(promise: Promise<ApiResult<T>>): Promise<T> {
  const res = await promise;
  if (!res.ok) throw new Error(res.error ?? `Request failed (${res.status})`);
  return res.data as T;
}
