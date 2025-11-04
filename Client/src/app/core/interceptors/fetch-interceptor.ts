import { AuthApiService } from '@api/apiServices/auth-api.service';
import { AuthService } from '@core/auth/auth.service';
import { inject, runInInjectionContext, EnvironmentInjector } from '@angular/core';

/**
 * Globální fetch interceptor, který automaticky řeší 401 → refresh → retry.
 * Musí se inicializovat v appConfig (viz níže).
 */
export function setupFetchInterceptor(env: EnvironmentInjector) {
  const originalFetch = window.fetch;

  window.fetch = async (input: RequestInfo | URL, init?: RequestInit): Promise<Response> => {
    // Volání fetchu v injekčním kontextu
    return await runInInjectionContext(env, async () => {
      const authApi = inject(AuthApiService);
      const authService = inject(AuthService);

      // Musíme mít vždy credentials: include
      const opts: RequestInit = { ...init, credentials: 'include' };

      let response = await originalFetch(input, opts);

      // 🔁 Pokud je 401, zkus refresh a retry
      if (response.status === 401) {
        console.warn('[fetch-interceptor] 401 detected → attempting refresh...');
        const refreshRes = await authApi.refresh();

        if (refreshRes.ok) {
          console.log('[fetch-interceptor] Refresh success → retrying request...');
          response = await originalFetch(input, opts);
        } else {
          console.error('[fetch-interceptor] Refresh failed → logging out');
          await authService.logout();
        }
      }

      return response;
    });
  };
}
