import { Injectable } from '@angular/core';
import { BaseApiService, ApiResult } from './base-api.service';
import { listUserLanguages, replaceUserLanguages } from '@generated/translators-language/translators-language';
import type { ListLanguagesResponse, ReplaceLanguagesRequest } from '@generated/model';

@Injectable({ providedIn: 'root' })
export class TranslatorsApiService extends BaseApiService {
  /**
   * Get languages known by a specific user
   * (ADMIN or the translator themselves)
   */
  async getUserLanguages(userId: string): Promise<ApiResult<string[]>> {
    try {
      const res = await listUserLanguages(userId, this.defaultOptions);

      if (res.status === 200 && Array.isArray(res.data)) {
        const mapped = res.data.map((lang) => String(lang));
        return this.handleResponse<string[]>({ ...res, data: mapped }, [200]);
      }

      return this.handleResponse<string[]>({ ...res, data: [] }, [200]);
    } catch (err: any) {
      return { ok: false, status: 500, error: err.message ?? 'Network error' };
    }
  }


  /**
   * Replace all languages of a specific user
   * (only the translator themselves can perform this)
   */
  async replaceUserLanguages(
    userId: string,
    payload: ReplaceLanguagesRequest
  ): Promise<ApiResult<string[]>> {
    try {
      const res = await replaceUserLanguages(userId, payload, this.defaultOptions);

      if (res.status === 200 && Array.isArray(res.data)) {
        const mapped = res.data.map((lang) => String(lang));
        return this.handleResponse<string[]>({ ...res, data: mapped }, [200]);
      }

      // fallback – pokud API vrátí nečekaný tvar
      return this.handleResponse<string[]>({ ...res, data: [] }, [200]);
    } catch (err: any) {
      return { ok: false, status: 500, error: err.message ?? 'Network error' };
    }
  }

}
