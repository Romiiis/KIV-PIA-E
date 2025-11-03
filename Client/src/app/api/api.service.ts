// src/app/core/api/api.service.ts
import { Injectable } from '@angular/core';
import { getCurrentUser } from '@generated/me/me';
import type { User } from '@generated/model';

export interface ApiResult<T> {
  ok: boolean;
  status: number;
  data?: T;
  error?: string;
}

@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly baseUrl = 'http://localhost:8080';

  /**
   * Get the currently authenticated user
   */
  async getCurrentUser(): Promise<ApiResult<User>> {
    try {
      const response = await getCurrentUser({ credentials: 'include' });

      if (response.status === 200) {
        return { ok: true, status: 200, data: response.data };
      }

      if (response.status === 401) {
        return { ok: false, status: 401, error: 'Unauthorized' };
      }

      return { ok: false, status: response.status, error: 'Unexpected error' };
    } catch (err: any) {
      console.error('[ApiService] getCurrentUser failed:', err);
      return { ok: false, status: 500, error: err.message ?? 'Network error' };
    }
  }
}
