import {injectMutation, injectQuery, injectQueryClient} from '@tanstack/angular-query-experimental';
import {UserMapper} from '@api/mappers/user.mapper';
import {loginUser, logoutUser, registerUser} from '@generated/auth/auth';
import {getCurrentUser} from '@generated/me/me';
import {UserDomain} from '@core/models/user.model';
import {UserRoleDomain} from '@core/models/userRole.model';


/**
 * Fetch the current logged-in user
 */
export function useMeQuery() {
  return injectQuery(() => ({
    queryKey: ['me'],
    queryFn: async (): Promise<UserDomain> => {
      const response = await getCurrentUser({credentials: 'include'});

      if (response.status !== 200)
        throw new Error('Failed to fetch current user');

      return UserMapper.mapApiUserToUser(response.data);
    },
    staleTime: 1000 * 60 * 5,
    refetchOnWindowFocus: true,
    enabled: false,
  }));
}


/**
 * Login mutation
 * @returns Mutation object
 */
export function useLoginMutation() {
  const queryClient = injectQueryClient();

  return injectMutation(() => ({
    mutationFn: async (credentials: { email: string; password: string }) => {
      const response = await loginUser(
        {
          emailAddress: credentials.email,
          password: credentials.password,
        },
      );

      if (response.status !== 200) throw new Error('Invalid credentials');
      return response.data.accessToken as string;
    },
    onSuccess: () => queryClient.invalidateQueries({queryKey: ['me']}),
  }));
}

/**
 * Register mutation
 * @returns Mutation object
 */
export function useRegisterMutation() {
  const queryClient = injectQueryClient();

  return injectMutation(() => ({
    mutationFn: async (data: { email: string; password: string; name: string; }) => {

      const response = await registerUser(
        {
          emailAddress: data.email,
          password: data.password,
          name: data.name,
        },
      );

      if (response.status !== 201) throw new Error('Registration failed');
      return response.data;
    },
    onSuccess: () => queryClient.invalidateQueries({queryKey: ['me']}),
  }));
}


/**
 * Logout mutation
 */
export function useLogoutMutation() {
  const queryClient = injectQueryClient();

  return injectMutation(() => ({
    mutationFn: async () => {
      const response = await logoutUser();
      if (response.status !== 204) throw new Error('Logout failed');
    },
    onSuccess: () => queryClient.clear(),
  }));
}
