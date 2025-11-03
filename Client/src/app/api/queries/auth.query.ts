// api/queries/auth.query.ts
import {injectMutation, injectQuery, injectQueryClient} from '@tanstack/angular-query-experimental';
import {AuthApiService} from '@api/apiServices/auth-api.service';
import {queryFromApi} from '@api/query.utils';
import type {LoginUserRequest, RegisterUserRequest} from '@generated/model';
import {inject} from '@angular/core';


function hasAuthCookies(accessName = 'accessToken', refreshName = 'refreshToken'): boolean {
  if (typeof document === 'undefined') return false;
  const cookies = document.cookie.split(';').map(c => c.trim());
  return cookies.some(c => c.startsWith(`${accessName}=`)) ||
    cookies.some(c => c.startsWith(`${refreshName}=`));
}

export function useMeQuery() {
  const authApi = inject(AuthApiService);

  return injectQuery(() => ({
    queryKey: ['me'],
    queryFn: () => queryFromApi(authApi.me()),
    staleTime: 1000 * 60 * 5,
    enabled: hasAuthCookies(),
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
