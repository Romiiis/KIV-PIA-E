import { Routes } from '@angular/router';
import { LayoutComponent } from './layout/layout.component';
import { AuthPageComponent } from '@features/auth/auth-page/auth-page.component';
import { CustomerPageComponent } from '@features/customer/customer-page/customer-page.component';
import { TranslatorPageComponent } from '@features/translator/translator-page/translator-page.component';
import { AdminPageComponent } from '@features/admin/admin-page/admin-page.component';
import { InitUserComponent } from '@features/init-user/init-user.component';
import { AuthGuard } from '@core/auth/auth.guard';
import { UserRoleDomain } from '@core/models/userRole.model';
import {RoleRedirectGuard} from '@core/auth/role.guard';

export const routes: Routes = [
  // 🔹 Root přesměruje podle role
  {
    path: '',
    canActivate: [RoleRedirectGuard],
    redirectTo: '', // placeholder — RoleGuard provede redirect dynamicky
    pathMatch: 'full',
  },

  // 🔹 Layout chráněný přihlášením
  {
    path: '',
    component: LayoutComponent,
    canActivate: [AuthGuard],
    children: [
      {
        path: 'customer',
        component: CustomerPageComponent,
        canActivate: [RoleRedirectGuard],
        data: { role: UserRoleDomain.CUSTOMER },
      },
      {
        path: 'translator',
        component: TranslatorPageComponent,
        canActivate: [RoleRedirectGuard],
        data: { role: UserRoleDomain.TRANSLATOR },
      },
      {
        path: 'admin',
        component: AdminPageComponent,
        canActivate: [RoleRedirectGuard],
        data: { role: UserRoleDomain.ADMINISTRATOR },
      },
    ],
  },

  // 🔹 Init user
  {
    path: 'init',
    component: InitUserComponent,
    canActivate: [AuthGuard],
  },

  // 🔹 Auth page
  {
    path: 'auth',
    component: AuthPageComponent,
  },

  // 🔹 Fallback
  {
    path: '**',
    redirectTo: '',
  },
];
