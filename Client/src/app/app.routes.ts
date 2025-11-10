import { Routes } from '@angular/router';
import { LayoutComponent } from './layout/layout.component';
import { AuthPageComponent } from '@features/auth/auth-page/auth-page.component';
import { CustomerPageComponent } from '@features/customer/customer-page/customer-page.component';
import { TranslatorPageComponent } from '@features/translator/translator-page/translator-page.component';
import { AdminPageComponent } from '@features/admin/admin-page/admin-page.component';
import { InitUserComponent } from '@features/init-user/init-user.component';
import {RoleRedirectGuard} from '@core/auth/role.guard';

export const routes: Routes = [
  // ROOT redirect based on role
  {
    path: '',
    canActivate: [RoleRedirectGuard],
    pathMatch: 'full',
    children: []
  },

  // Layout with role-based children
  {
    path: '',
    component: LayoutComponent,
    canActivate: [RoleRedirectGuard],
    children: [
      { path: 'customer', component: CustomerPageComponent },
      { path: 'translator', component: TranslatorPageComponent },
      { path: 'admin', component: AdminPageComponent },
    ],
  },
  {
    path: 'init',
    component: InitUserComponent,
    canActivate: [RoleRedirectGuard],
  },

  // AUTH page for login/signup
  {
    path: 'auth',
    canActivate: [RoleRedirectGuard],
    component: AuthPageComponent,
  },

  // FALLBACK route - redirect based on role
  {
    canActivate: [RoleRedirectGuard],
    path: '**',
    redirectTo: '',
  },
];
