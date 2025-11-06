import {computed, effect, Injectable, signal} from '@angular/core';
import {UserDomain} from '@core/models/user.model';
import {
  useLoginMutation,
  useLogoutMutation,
  useMeQuery,
  useRefresh,
  useRegisterMutation
} from '@api/queries/auth.query';
import {injectQueryClient} from '@tanstack/angular-query-experimental';
import {QK} from '@api/queries/query-keys';
import {setRefreshHandler} from '@api/axios-instance';


/**
 * Service to manage user authentication state and actions.
 */
@Injectable({providedIn: 'root'})
export class AuthManager {

  // Signal to hold the current user data
  readonly user = signal<UserDomain | undefined>(undefined);

  // Computed signal to check if user is logged in
  readonly isLoggedIn = computed(() => !!this.user());

  // Queries and mutations for authentication operations
  private readonly meQuery = useMeQuery();
  private readonly loginMutation = useLoginMutation();
  private readonly logoutMutation = useLogoutMutation();
  private readonly registerMutation = useRegisterMutation();
  private readonly refreshTokenMutation = useRefresh();
  private readonly queryClient = injectQueryClient();

  /**
   * Constructor to set up effects and refresh handler.
   */
  constructor() {
    effect(() => {
      const me = this.meQuery.data();
      if (me) this.user.set(me);
    });

    // Set up token refresh handler for API requests
    setRefreshHandler(async () => {
      try {
        await this.refreshTokenMutation.mutateAsync();
        return true;
      } catch (err) {
        console.error('[AuthManager] Token refresh failed', err);
        return false;
      }
    });
  }

  /**
   * Initializes the authentication state by fetching current user data.
   */
  async init(): Promise<void> {
    const me = await this.meQuery.refetch(); //
    if (me.data) this.user.set(me.data);
  }

  /**
   * Logs in a user with the provided email and password.
   * @param email User's email.
   * @param password User's password.
   */
  async login(email: string, password: string) {
    await this.loginMutation.mutateAsync({email, password});
    await this.meQuery.refetch();
  }

  /**
   * Registers a new user with the provided name, email, and password.
   * @param name User's name.
   * @param email User's email.
   * @param password User's password.
   */
  async register(name: string, email: string, password: string) {
    await this.registerMutation.mutateAsync({name, email, password});
    await this.meQuery.refetch();

  }

  /**
   * Logs out the current user.
   */
  async logout() {
    await this.logoutMutation.mutateAsync();
    this.user.set(undefined);
    this.queryClient.removeQueries({queryKey: QK.me});
  }

  /**
   * Refreshes the current user data from the server.
   */
  async refreshUserData() {
    const me = await this.meQuery.refetch();
    if (me.data) this.user.set(me.data);

  }
}
