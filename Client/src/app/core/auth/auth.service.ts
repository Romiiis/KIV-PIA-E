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
import {AuthApiService} from '@api/apiServices/auth-api.service';
import {UserRoleDomain} from '@core/models/userRole.model';
import {firstValueFrom} from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AuthService {


  readonly user = signal<UserDomain | undefined>(undefined);
  readonly isLoggedIn = computed(() => !!this.user());
  readonly role = computed(() => this.user()?.role);

  private readonly queryClient = injectQueryClient();
  private readonly loginMutation = useLoginMutation();
  private readonly registerMutation = useRegisterMutation();
  private readonly logoutMutation = useLogoutMutation();
  private readonly meQuery = useMeQuery();
  private readonly refreshTokenMutation = useRefresh();

  constructor(private authApi: AuthApiService) {
    (globalThis as any).__authService = this;
    effect(() => {
      const me = this.meQuery.data();
      if (me) this.user.set(me);
    });
  }

  async checkAndRestoreSession(): Promise<boolean> {
    if (this.isLoggedIn()) return true;

    try {
      const me = await firstValueFrom(this.authApi.me());
      this.user.set(me);
      return true;

    } catch (err: any) {
      if (err?.status === 401 || err?.response?.status === 401) {
        try {
          await firstValueFrom(this.authApi.refreshToken());
          const refreshed = await firstValueFrom(this.authApi.me());
          this.user.set(refreshed);
          return true;
        } catch (refreshErr) {
          console.warn('[Auth] Refresh failed ❌', refreshErr);
          this.user.set(undefined);
          return false;
        }
      }

      console.error('[Auth] checkAndRestoreSession failed:', err);
      this.user.set(undefined);
      return false;
    }
  }


  async refetchCurrentUser(): Promise<void> {
    const me = await this.meQuery.refetch();
    if (me.data) {
      this.user.set(me.data);
    }

  }

  async login(email: string, password: string): Promise<void> {
    const user = await this.loginMutation.mutateAsync({ email, password });
    if (user) {
      this.user.set(user);
      this.queryClient.setQueryData(['me'], user);
    }
  }

  async register(name: string, email: string, password: string): Promise<void> {
    const user = await this.registerMutation.mutateAsync({ name, email, password });
    if (user) {
      this.user.set(user);
      this.queryClient.setQueryData(['me'], user);
    }
  }

  async logout(): Promise<void> {
    await this.logoutMutation.mutateAsync();
    this.user.set(undefined);
    this.queryClient.removeQueries({ queryKey: ['me'] });
  }

  get currentUser() { return this.user(); }
  get currentRole() { return this.role(); }

  resolvePath(role: UserRoleDomain | undefined): string {
    switch (role) {
      case UserRoleDomain.CUSTOMER: return '/customer';
      case UserRoleDomain.TRANSLATOR: return '/translator';
      case UserRoleDomain.ADMINISTRATOR: return '/admin';
      default: return '/init';
    }
  }
}
