import { Injectable, signal, computed, effect } from '@angular/core';
import { useLoginMutation, useLogoutMutation, useRegisterMutation, useMeQuery } from '@api/queries/auth.query';
import { UserDomain } from '@core/models/user.model';
import { UserRoleDomain } from '@core/models/userRole.model';
import {injectQueryClient} from '@tanstack/angular-query-experimental';

@Injectable({ providedIn: 'root' })
export class AuthService {
  readonly user = signal<UserDomain | undefined>(undefined);
  readonly isLoggedIn = computed(() => !!this.user());
  readonly role = computed(() => this.user()?.role);
  readonly isInitializing = signal(true);


  private readonly queryClient = injectQueryClient();
  private readonly loginMutation = useLoginMutation();
  private readonly registerMutation = useRegisterMutation();
  private readonly logoutMutation = useLogoutMutation();
  private readonly meQuery = useMeQuery();

  constructor() {
    effect(() => {
      const me = this.meQuery.data();
      if (me) {
        this.user.set(me);
      }
    });
  }

  async initialize(): Promise<void> {
    this.isInitializing.set(true);
    const result = await this.meQuery.refetch();
    if (result.data) this.user.set(result.data);
    else this.user.set(undefined);
    this.isInitializing.set(false);
  }


  async login(email: string, password: string): Promise<void> {
    await this.loginMutation.mutateAsync({ emailAddress: email, password });
    const result = await this.meQuery.refetch();
    if (result.data) {
      this.user.set(result.data);
    }
  }

  async register(email: string, password: string, name: string): Promise<void> {
    await this.registerMutation.mutateAsync({ emailAddress: email, password, name });
    const result = await this.meQuery.refetch();
    if (result.data) {
      this.user.set(result.data);
    }  }

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

  async refreshUser(): Promise<UserDomain | undefined> {
    const result = await this.meQuery.refetch();
    if (result.data) {
      this.user.set(result.data);
      return result.data;
    }
    return undefined;
  }

}
