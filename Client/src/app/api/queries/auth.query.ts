  import {inject} from '@angular/core';
  import {
    injectQuery,
    injectMutation,
    injectQueryClient,
  } from '@tanstack/angular-query-experimental';
  import {AuthApiService} from '@api/apiServices/auth-api.service';
  import {QK} from './query-keys';
  import {toPromise} from './utils';


  /**
   * Get current logged in user (me).
   * @return Query with user data.
   */
  export function useMeQuery() {
    const api = inject(AuthApiService);
    return injectQuery(() => ({
      queryKey: QK.me,
      queryFn: () => toPromise(api.me()),
      enabled: false,
      retry: 0,
      refetchOnWindowFocus: false,
      refetchOnReconnect: false,
    }));
  }

  /**
   * Mutation: login user.
   * @return Mutation for logging in.
   */
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


  /**
   * Mutation: register new user.
   * @return Mutation for registering.
   */
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

  /**
   * Mutation: logout user.
   * @return Mutation for logging out.
   */
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

  /**
   * Mutation: refresh token.
   * @return Mutation for refreshing token.
   */
  export function useRefresh() {
    const api = inject(AuthApiService);
    const qc = injectQueryClient();

    return injectMutation(() => ({
      mutationFn: () => toPromise(api.refreshToken()),
      onSuccess: () => {
      },
    }));
  }
