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
import {UserRoleDomain} from '@core/models/userRole.model';
import {setRefreshHandler} from '@api/axios-instance';



@Injectable({providedIn: 'root'})
export class AuthManager {
  readonly user = signal<UserDomain | undefined>(undefined);
  readonly isLoggedIn = computed(() => !!this.user());

  private readonly meQuery = useMeQuery();
  private readonly loginMutation = useLoginMutation();
  private readonly logoutMutation = useLogoutMutation();
  private readonly registerMutation = useRegisterMutation();
  private readonly refreshTokenMutation = useRefresh();
  private readonly queryClient = injectQueryClient();

  constructor() {
    effect(() => {
      const me = this.meQuery.data();
      if (me) this.user.set(me);
    });

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

  async init(): Promise<void> {
    const me = await this.meQuery.refetch(); //
    if (me.data) this.user.set(me.data);
  }

  async login(email: string, password: string) {
    await this.loginMutation.mutateAsync({email, password});
    await this.meQuery.refetch();
  }

  async register(name: string, email: string, password: string) {
    await this.registerMutation.mutateAsync({name, email, password});
    await this.meQuery.refetch();

  }

  async logout() {
    await this.logoutMutation.mutateAsync();
    this.user.set(undefined);
    this.queryClient.removeQueries({queryKey: QK.me});
  }

  async refreshToken() {
    // This method can be used to refresh the token if needed
    await this.refreshTokenMutation.mutateAsync();
  }


  async refreshUserData() {
    const me = await this.meQuery.refetch();
    if (me.data) this.user.set(me.data);

  }
}
