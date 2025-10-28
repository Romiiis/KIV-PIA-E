import { inject, Injectable, Injector, runInInjectionContext, computed, signal } from '@angular/core';
import { useLoginMutation, useLogoutMutation, useMeQuery, useRegisterMutation } from '@api/queries/auth.query';
import { UserDomain } from '@core/models/user.model';
import { UserRoleDomain } from '@core/models/userRole.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly injector = inject(Injector);

  readonly user = runInInjectionContext(this.injector, () => {
    const meQuery = useMeQuery();
    return computed(() => meQuery.data());
  });

  readonly isLoggedIn = computed(() => !!this.user());
  readonly role = computed(() => this.user()?.role);


  async login(email: string, password: string): Promise<UserDomain | undefined> {
    return runInInjectionContext(this.injector, async () => {
      const loginMutation = useLoginMutation();
      const meQuery = useMeQuery();

      return new Promise((resolve, reject) => {
        loginMutation.mutate(
          { email, password },
          {
            onSuccess: async () => {
            },
            onError: (err) => {
              console.error('Login failed', err);
              reject(err);
            },
          }
        );
      });
    });
  }

  // ------------------------------
  // 👤 REGISTRACE
  // ------------------------------
  async register(data: {
    email: string;
    password: string;
    name: string;
    type: UserRoleDomain;
  }): Promise<boolean> {
    return runInInjectionContext(this.injector, async () => {
      const registerMutation = useRegisterMutation();

      return new Promise((resolve, reject) => {
        registerMutation.mutate(data, {
          onSuccess: () => resolve(true),
          onError: (err) => {
            console.error('Registration failed', err);
            reject(false);
          },
        });
      });
    });
  }

  // ------------------------------
  // 🚪 LOGOUT
  // ------------------------------
  async logout(): Promise<boolean> {
    return runInInjectionContext(this.injector, async () => {
      const logoutMutation = useLogoutMutation();

      return new Promise((resolve, reject) => {
        logoutMutation.mutate(undefined, {
          onSuccess: () => resolve(true),
          onError: (err) => {
            console.error('Logout failed', err);
            reject(err);
          },
        });
      });
    });
  }

  // ------------------------------
  // 📜 GETTERS
  // ------------------------------
  get currentUser(): UserDomain | undefined {
    return this.user();
  }

  get currentRole(): UserRoleDomain | undefined {
    return this.role();
  }
}
