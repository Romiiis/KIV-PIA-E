import {Routes} from '@angular/router';
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
    loadComponent: () => import('./layout/layout.component').then(m => m.LayoutComponent),
    canActivate: [RoleRedirectGuard],
    children: [
      {
        path: 'customer',
        loadComponent: () => import('@features/customer/customer-page/customer-page.component').then(m => m.CustomerPageComponent)
      },
      {
        path: 'translator',
        loadComponent: () => import('@features/translator/translator-page/translator-page.component').then(m => m.TranslatorPageComponent)
      },
      {
        path: 'admin',
        loadComponent: () => import('@features/admin/admin-page/admin-page.component').then(m => m.AdminPageComponent)
      },
    ],
  },
  {
    path: 'init',
    loadComponent: () => import('@features/init-user/init-user.component').then(m => m.InitUserComponent),
    canActivate: [RoleRedirectGuard],
  },

  // AUTH page for login/signup
  {
    path: 'auth',
    canActivate: [RoleRedirectGuard],
    loadComponent: () => import('@features/auth/auth-page/auth-page.component').then(m => m.AuthPageComponent),
  },

  // FALLBACK route - redirect based on role
  {
    canActivate: [RoleRedirectGuard],
    path: '**',
    redirectTo: '',
  },
];
