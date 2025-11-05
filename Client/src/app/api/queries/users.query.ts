import { inject } from '@angular/core';
import {
  injectMutation,
  injectQuery,
  injectQueryClient,
} from '@tanstack/angular-query-experimental';
import { UsersApiService } from '@api/apiServices/users-api.service';
import { QK } from './query-keys';
import { toPromise } from './utils';
import { UserRoleDomain } from '@core/models/userRole.model';

/**
 * Query: seznam všech uživatelů
 */
export function useListUsersQuery() {
  const api = inject(UsersApiService);
  return injectQuery(() => ({
    queryKey: QK.users,
    queryFn: () => toPromise(api.listAll()),
    staleTime: 60_000,
  }));
}

/**
 * Query: detail uživatele podle ID.
 */
export function useGetUserQuery(id: string) {
  const api = inject(UsersApiService);
  return injectQuery(() => ({
    queryKey: QK.user(id),
    queryFn: () => toPromise(api.detail(id)),
    enabled: !!id,
  }));
}

/**
 * Mutation: změna role uživatele (inicializace účtu).
 */
export function useChangeUserRoleMutation() {
  const api = inject(UsersApiService);
  const qc = injectQueryClient();

  return injectMutation(() => ({
    mutationFn: (input: { id: string; role: UserRoleDomain; languages?: string[] }) =>
      toPromise(api.changeRole(input.id, input.role, input.languages)),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: QK.me });
    },
  }));
}
