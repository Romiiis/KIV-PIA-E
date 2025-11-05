import {inject} from '@angular/core';
import {injectMutation} from '@tanstack/angular-query-experimental';
import {UsersApiService} from '@api/apiServices/users-api.service';
import {toPromise} from './utils';
import {UserRoleDomain} from '@core/models/userRole.model';

/**
 * Mutation: List all users.
 */
export function useListUsersMutation() {
  const api = inject(UsersApiService);

  return injectMutation(() => ({
    mutationFn: () => toPromise(api.listAll()),
  }));
}

/**
 * Mutation: Get user details by ID.
 */
export function useGetUserMutation() {
  const api = inject(UsersApiService);

  return injectMutation(() => ({
    mutationFn: (id: string) => toPromise(api.detail(id)),
  }));
}

/**
 * Mutation: Change user role.
 * @return Mutation hook to change a user's role.
 */
export function useChangeUserRoleMutation() {
  const api = inject(UsersApiService);

  return injectMutation(() => ({
    mutationFn: (input: { id: string; role: UserRoleDomain; languages?: string[] }) =>
      toPromise(api.changeRole(input.id, input.role, input.languages)),
  }));
}
