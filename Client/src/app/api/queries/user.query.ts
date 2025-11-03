import { injectQuery, injectMutation, injectQueryClient } from '@tanstack/angular-query-experimental';
import { inject } from '@angular/core';
import { UsersApiService } from '@api/apiServices/users-api.service';
import { UserMapper } from '@api/mappers/user.mapper';
import { queryFromApi } from '@api/query.utils';
import { UserDomain } from '@core/models/user.model';
import { UserRoleDomain } from '@core/models/userRole.model';

/**
 * Fetch all users (ADMIN)
 * Returns List<UserDomain>
 */
export function useUsersQuery() {
  const usersApi = inject(UsersApiService);

  return injectQuery(() => ({
    queryKey: ['users'],
    queryFn: async (): Promise<UserDomain[]> => {
      const result = await queryFromApi(usersApi.getAll());
      if (!result) return [];

      // API response je ListUsersResponse = User[]
      return result.map(UserMapper.mapApiUserToUser);
    },
  }));
}

/**
 * Fetch single user (any role)
 */
export function useUserQuery(userId: string) {
  const usersApi = inject(UsersApiService);

  return injectQuery(() => ({
    queryKey: ['user', userId],
    queryFn: async (): Promise<UserDomain | undefined> => {
      const result = await queryFromApi(usersApi.getById(userId));
      return result ? UserMapper.mapApiUserToUser(result) : undefined;
    },
  }));
}

/**
 * Change user role mutation (e.g. during initialization)
 */
export function useChangeUserRoleMutation() {
  const usersApi = inject(UsersApiService);
  const queryClient = injectQueryClient();

  return injectMutation(() => ({
    mutationFn: async (input: { id: string; role: UserRoleDomain, languages?: string[] }) => {
      const payload = { role: input.role , languages: input.languages};
      return queryFromApi(usersApi.changeRole(input.id, payload));
    },
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['me'] }),
  }));
}
