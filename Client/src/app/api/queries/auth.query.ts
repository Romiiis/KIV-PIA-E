  import {inject} from '@angular/core';
  import {
    injectQuery,
    injectMutation,
    injectQueryClient,
  } from '@tanstack/angular-query-experimental';
  import {AuthApiService} from '@api/apiServices/auth-api.service';
  import {QK} from './query-keys';
  import {toPromise} from './utils';


  export function useMeQuery() {
    const api = inject(AuthApiService);
    return injectQuery(() => ({
      queryKey: QK.me,
      queryFn: () => toPromise(api.me()),
      staleTime: 60_000,
      retry: 1,
    }));
  }

  export function useLoginMutation() {
    const api = inject(AuthApiService);
    const qc = injectQueryClient();

    return injectMutation(() => ({
      mutationFn: (input: { email: string; password: string }) =>
        toPromise(api.login(input.email, input.password)),
      onSuccess: (user) => {
        qc.setQueryData(QK.me, user);
      },
    }));
  }

  export function useRegisterMutation() {
    const api = inject(AuthApiService);
    const qc = injectQueryClient();

    return injectMutation(() => ({
      mutationFn: (input: { name: string; email: string; password: string }) =>
        toPromise(api.register(input.name, input.email, input.password)),
      onSuccess: (user) => {
        qc.setQueryData(QK.me, user);
      },
    }));
  }

  export function useLogoutMutation() {
    const api = inject(AuthApiService);
    const qc = injectQueryClient();

    return injectMutation(() => ({
      mutationFn: () => toPromise(api.logout()),
      onSuccess: () => {
        qc.removeQueries({queryKey: QK.me});
        qc.clear();
      },
    }));
  }

  export function useRefresh() {
    const api = inject(AuthApiService);
    const qc = injectQueryClient();

    return injectMutation(() => ({
      mutationFn: () => toPromise(api.refreshToken()),
      onSuccess: () => {
      },
    }));
  }
