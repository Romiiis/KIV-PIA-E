// api/queries/auth.query.ts
import {injectMutation, injectQuery, injectQueryClient} from '@tanstack/angular-query-experimental';
import {AuthApiService} from '@api/apiServices/auth-api.service';
import {queryFromApi} from '@api/query.utils';
import type {LoginUserRequest, RegisterUserRequest} from '@generated/model';
import {inject} from '@angular/core';

export function useMeQuery(enabled = false) {
  const authApi = inject(AuthApiService);

  return injectQuery(() => ({
    queryKey: ['me'],
    queryFn: async () => {
      try {
        return await queryFromApi(authApi.me());
      } catch (err: any) {
        if (err.status === 401 || err.status === 403) {
          // Unauthorized or Forbidden - return undefined user
          return null;
        }
        throw err;
      }
    },
    staleTime: 1000 * 60 * 5,
    enabled: enabled,
    retry: false,
    refetchOnWindowFocus: true,
  }));
}

export function useLoginMutation() {
  const authApi = inject(AuthApiService);
  const queryClient = injectQueryClient();

  return injectMutation(() => ({
    mutationFn: (payload: LoginUserRequest) => queryFromApi(authApi.login(payload)),
    onSuccess: () => queryClient.invalidateQueries({queryKey: ['me']}),
  }));
}

export function useRegisterMutation() {
  const authApi = inject(AuthApiService);
  const queryClient = injectQueryClient();

  return injectMutation(() => ({
    mutationFn: (payload: RegisterUserRequest) => queryFromApi(authApi.register(payload)),
    onSuccess: () => queryClient.invalidateQueries({queryKey: ['me']}),
  }));
}

export function useLogoutMutation() {
  const authApi = inject(AuthApiService);
  const queryClient = injectQueryClient();

  return injectMutation(() => ({
    mutationFn: () => queryFromApi(authApi.logout()),
    onSuccess: () => {
      queryClient.clear();
    },
  }));


}
