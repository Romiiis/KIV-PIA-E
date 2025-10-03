import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree} from '@angular/router';
import {AuthService} from '@core/auth/auth.service';

@Injectable({ providedIn: 'root' })
export class RoleRedirectGuard implements CanActivate {
  constructor(private auth: AuthService, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean | UrlTree {
    const role = this.auth.getRole();
    console.log(role)
    // není přihlášený -> na login, ALE pokud už je na /auth, tak ho nech tam
    if (!role) {
      if (state.url === '/auth') {
        return true; // už je na login page, necyklit
      }
      return this.router.parseUrl('/auth');
    }

    // role známá -> pustíme ho jen na správnou stránku
    if (role === 'customer' && state.url.startsWith('/customers')) return true;
    if (role === 'translator' && state.url.startsWith('/dashboard')) return true;
    // if (role === 'admin' && state.url.startsWith('/admin')) return true;

    // root nebo fallback -> přesměruj podle role
    if (state.url === '/' || route.routeConfig?.path === '**') {
      switch (role) {
        case 'customer': return this.router.parseUrl('/customers');
        case 'translator': return this.router.parseUrl('/dashboard');
        // case 'admin': return this.router.parseUrl('/admin');
      }
    }

    // cokoliv jiného -> fallback na login
    return this.router.parseUrl('/auth');
  }
}
