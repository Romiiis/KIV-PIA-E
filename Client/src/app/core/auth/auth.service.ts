import {Injectable, signal, computed, effect, inject} from '@angular/core';
import {
  useLoginMutation,
  useLogoutMutation,
  useRegisterMutation,
  useMeQuery,
} from '@api/queries/auth.query';
import { UserDomain } from '@core/models/user.model';
import { UserRoleDomain } from '@core/models/userRole.model';
import {injectQueryClient} from '@tanstack/angular-query-experimental';
import { AuthApiService } from '@api/apiServices/auth-api.service';


@Injectable({ providedIn: 'root' })
export class AuthService {
  readonly user = signal<UserDomain | undefined>(undefined);
  readonly isLoggedIn = computed(() => !!this.user());
  readonly role = computed(() => this.user()?.role);
  readonly isInitializing = signal(true);
  readonly isRefreshing = signal(false);

  private refreshPromise: Promise<void> | null = null;


  private readonly queryClient = injectQueryClient();
  private readonly loginMutation = useLoginMutation();
  private readonly registerMutation = useRegisterMutation();
  private readonly logoutMutation = useLogoutMutation();
  private readonly meQuery = useMeQuery();



  constructor(private authApi: AuthApiService) {
    effect(() => {
      const me = this.meQuery.data();
      if (me) {
        this.user.set(me);
      }
    });
  }

  async initialize(): Promise<void> {
    this.isInitializing.set(true);
    try {
      const result = await this.meQuery.refetch();
      if (result.data) {
        this.user.set(result.data);
      } else {
        this.user.set(undefined);
      }
    } catch (error) {
      this.user.set(undefined);
  }
    this.isInitializing.set(false);
  }



  async refreshIfNeeded(): Promise<void> {
    // Pokud už probíhá refresh → počkej na stejný Promise (nepouštěj víckrát)
    if (this.refreshPromise) {
      console.log('[AuthService] Waiting for ongoing refresh...');
      return this.refreshPromise;
    }

    this.isRefreshing.set(true);
    this.refreshPromise = (async () => {
      try {
        console.log('[AuthService] Attempting token refresh...');
        const refreshRes = await this.authApi.refresh();

        if (refreshRes.ok) {
          console.log('[AuthService] Token refresh successful');
          const meRes = await this.authApi.me();

          if (meRes.ok && meRes.data) {
            this.user.set(meRes.data);
          }
        } else {
          console.warn('[AuthService] Refresh failed');
          this.user.set(undefined);
        }
      } catch (err) {
        console.error('[AuthService] Refresh exception:', err);
        this.user.set(undefined);
      } finally {
        this.isRefreshing.set(false);
        this.refreshPromise = null;
      }
    })();

    return this.refreshPromise;
  }



  // Login user and update the user signal
  async login(email: string, password: string): Promise<void> {
    await this.loginMutation.mutateAsync({ emailAddress: email, password });
    const result = await this.meQuery.refetch();
    if (result.data) {
      this.user.set(result.data);
    }
  }

  // Register user and update the user signal
  async register(email: string, password: string, name: string): Promise<void> {
    await this.registerMutation.mutateAsync({ emailAddress: email, password, name });
    const result = await this.meQuery.refetch();
    if (result.data) {
      this.user.set(result.data);
    }  }

  // Logout user and clear the user signal
  async logout(): Promise<void> {
    await this.logoutMutation.mutateAsync(undefined);
    this.user.set(undefined);
    this.queryClient.removeQueries({ queryKey: ['me'] });

  }

  get currentUser(): UserDomain | undefined {
    return this.user();
  }

  get currentRole(): UserRoleDomain | undefined {
    return this.role();
  }

  resolvePath(userRole: UserRoleDomain | undefined): string {
    switch (userRole) {
      case UserRoleDomain.CUSTOMER:
        return '/customer';
      case UserRoleDomain.TRANSLATOR:
        return '/translator';
      case UserRoleDomain.ADMINISTRATOR:
        return '/admin';
      default:
        return '/init';
    }
  }

  /**
   * Refresh the current user data
   * Manually refetches the user data from the server and updates the user signal.
   */
  async refreshUser(): Promise<UserDomain | undefined> {
    const result = await this.meQuery.refetch();
    if (result.data) {
      this.user.set(result.data);
      return result.data;
    }
    return undefined;
  }

}
