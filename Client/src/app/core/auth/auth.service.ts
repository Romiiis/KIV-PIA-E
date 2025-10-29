import {computed, effect, inject, Injectable, Injector, runInInjectionContext, signal} from '@angular/core';
import {useLoginMutation, useLogoutMutation, useMeQuery, useRegisterMutation} from '@api/queries/auth.query';
import {UserDomain} from '@core/models/user.model';
import {UserRoleDomain} from '@core/models/userRole.model';
import {Router} from '@angular/router';

@Injectable({providedIn: 'root'})
export class AuthService {
  private readonly injector = inject(Injector);

  readonly user = signal<UserDomain | undefined>(undefined);


  readonly isLoggedIn = computed(() => !!this.user());
  readonly role = computed(() => this.user()?.role);

  constructor(private router: Router) {

    // Effect to monitor user role and redirect to /init if role is undefined
    runInInjectionContext(this.injector, () => {
      effect(() => {
        const user = this.user();
        if (user && user.role === undefined) {
          console.log('effect fired, user =', this.user());
          //this.router.navigateByUrl('/init');
        }
      });
    });
  }


  async login(email: string, password: string): Promise<UserDomain | undefined> {
    return runInInjectionContext(this.injector, async () => {
      const loginMutation = useLoginMutation();
      const meQuery = useMeQuery();

      return new Promise((resolve, reject) => {
        loginMutation.mutate(
          {email, password},
          {
            onSuccess: async () => {
              const me = await meQuery.refetch();
              this.user.set(me.data);
              resolve(me.data);
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


  async register(email: string, password: string, name: string): Promise<UserDomain | undefined>  {
    return runInInjectionContext(this.injector, async () => {
      const registerMutation = useRegisterMutation();
      const meQuery = useMeQuery();

      return new Promise((resolve, reject) => {
        registerMutation.mutate({email, password, name}, {
          onSuccess: async () => {
            const me = await meQuery.refetch();
            this.user.set(me.data);
            resolve(me.data);

          },
          onError: (err) => {
            console.error('Login failed', err);
            reject(err);
          },
        });
      });
    });
  }


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

  get currentUser(): UserDomain | undefined {
    return this.user();
  }

  get currentRole(): UserRoleDomain | undefined {
    return this.role();
  }
}
