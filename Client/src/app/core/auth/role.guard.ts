import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree} from '@angular/router';
import {AuthService} from '@core/auth/auth.service';
import {UserRoleDomain} from '@core/models/userRole.model';

@Injectable({ providedIn: 'root' })
export class RoleRedirectGuard implements CanActivate {
  constructor(private auth: AuthService, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean | UrlTree {

    // Get the role of the current user
    const role = this.auth.getRole();

    // If no role (not logged in) -> just route to login page
    if (!role) {
      if (state.url === '/auth') {
        return true; // If already going to auth page, allow it (do not redirect in a loop)
      }
      // Otherwise, redirect to auth page
      return this.router.parseUrl('/auth');
    }

    // If user has a role, check if they are going to the right place
    if (role === UserRoleDomain.CUSTOMER && state.url.startsWith('/customer')) return true;
    if (role === UserRoleDomain.TRANSLATOR && state.url.startsWith('/translator')) return true;
    if (role === UserRoleDomain.ADMINISTRATOR && state.url.startsWith('/admin')) return true;

    // If user is going to root or wildcard, redirect based on role
    if (state.url === '/' || route.routeConfig?.path === '**') {
      switch (role) {
        case UserRoleDomain.CUSTOMER: return this.router.parseUrl('/customer');
        case UserRoleDomain.TRANSLATOR: return this.router.parseUrl('/translator');
        case UserRoleDomain.ADMINISTRATOR: return this.router.parseUrl('/admin');
      }
    }

    // If none of the above, redirect to auth page (could also show a "not authorized" page)
    return this.router.parseUrl('/auth');
  }
}
