import {Injectable} from '@angular/core';
import {CanActivate, Router, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree} from '@angular/router';
import {AuthManager} from '@core/auth/auth.manager';
import {AuthRoutingHelper} from '@core/auth/auth-routing.helper';


@Injectable({providedIn: 'root'})
export class RoleRedirectGuard implements CanActivate {
  constructor(private auth: AuthManager, private router: Router) {
  }

  async canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Promise<boolean | UrlTree> {
    const {isLoggedIn, user} = this.auth;
    const currentUrl = state.url;

    // Not logged in → auth
    if (!isLoggedIn()) {

      const isAuthPath = currentUrl.startsWith(AuthRoutingHelper.AUTH_PATH);

      if (isAuthPath) {
        return true;
      }
      const urlTree = this.router.parseUrl(currentUrl);
      const queryParams = urlTree.queryParams;

      return AuthRoutingHelper.redirectToAuth(this.router, queryParams);
    }

    const role = user()?.role;

    // logged in but no role → init
    if (!role) {
      if (state.url === AuthRoutingHelper.INIT_PATH) {
        return true;
      }
      return AuthRoutingHelper.redirectToInit(this.router);
    }

    // logged in with role → check access
    const allowedPath = AuthRoutingHelper.pathForRole(role);
    if (state.url.startsWith(allowedPath)) {
      return true;
    }

    if (state.url === AuthRoutingHelper.INIT_PATH) {
      return AuthRoutingHelper.redirectToRole(this.router, role);
    }

    // redirect to role's default path
    if (state.url === '/' || route.routeConfig?.path === '**') {
      return AuthRoutingHelper.redirectToRole(this.router, role);
    }

    // fallback – go to home
    return this.router.parseUrl('/');
  }
}
