// base-api.service.ts
import { Injectable } from '@angular/core';

export interface ApiResult<T> {
  ok: boolean;
  status: number;
  data?: T;
  error?: string;
}

@Injectable()
export abstract class BaseApiService {
  protected readonly defaultOptions: RequestInit = {
    credentials: 'include',
  };

  protected handleResponse<T>(
    response: { status: number; data?: any },
    okStatuses: number[] = [200, 201, 204]
  ): ApiResult<T> {
    if (okStatuses.includes(response.status)) {
      return {
        ok: true,
        status: response.status,
        data: response.data as T,
      };
    }

    return {
      ok: false,
      status: response.status,
      error: this.mapErrorMessage(response.status),
    };
  }

  private mapErrorMessage(status: number): string {
    switch (status) {
      case 400:
        return 'Bad request';
      case 401:
        return 'Unauthorized';
      case 403:
        return 'Forbidden';
      case 404:
        return 'Not found';
      case 409:
        return 'Conflict';
      case 500:
        return 'Internal server error';
      default:
        return `Unexpected error (${status})`;
    }
  }
}
