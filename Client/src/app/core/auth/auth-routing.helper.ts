import {Params, Router, UrlTree} from '@angular/router';
import {UserRoleDomain} from '@core/models/userRole.model';

/**
 * Helper for authentication routing based on user roles.
 */
export class AuthRoutingHelper {
  static AUTH_PATH = '/auth';
  static INIT_PATH = '/init';

  static CUSTOMER_PATH = '/customer';
  static TRANSLATOR_PATH = '/translator'
  static ADMIN_PATH = '/admin';

  /**
   * Get path for a given user role.
   * @param role User role
   * @returns Path string
   */
  static pathForRole(role?: UserRoleDomain): string {
    switch (role) {
      case UserRoleDomain.CUSTOMER:
        return this.CUSTOMER_PATH;
      case UserRoleDomain.TRANSLATOR:
        return this.TRANSLATOR_PATH;
      case UserRoleDomain.ADMINISTRATOR:
        return this.ADMIN_PATH;
      default:
        return AuthRoutingHelper.INIT_PATH;
    }
  }

  /** ================== Redirect methods ================= */

  /**
   * Redirect to login (FOR GUARDS).
   */
  static redirectToAuth(router: Router, queryParams?: Params): UrlTree {
    return router.createUrlTree([AuthRoutingHelper.AUTH_PATH], {queryParams});
  }

  /**
   * Redirect to init page (FOR GUARDS).
   */
  static redirectToInit(router: Router): UrlTree {
    return router.parseUrl(AuthRoutingHelper.INIT_PATH);
  }

  /**
   * Redirect to role-specific path (FOR GUARDS).
   */
  static redirectToRole(router: Router, role: UserRoleDomain): UrlTree {
    return router.parseUrl(AuthRoutingHelper.pathForRole(role));
  }

  /** ================== Navigation methods ================= */


  /**
   * Navigate to login page.
   * @param router Router instance
   */
  static navigateToAuth(router: Router): Promise<boolean> {
    return router.navigateByUrl(this.AUTH_PATH);
  }

  /**
   * Navigate to init page.
   * @param router Router instance
   */
  static navigateToInit(router: Router): Promise<boolean> {
    return router.navigateByUrl(this.INIT_PATH);
  }

  /**
   * Navigate to role-specific page.
   * @param router Router instance
   * @param role User role
   */
  static navigateToRole(router: Router, role: UserRoleDomain): Promise<boolean> {
    return router.navigateByUrl(this.pathForRole(role));
  }
}
