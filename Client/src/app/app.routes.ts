import {Routes} from '@angular/router';
import {LayoutComponent} from './layout/layout.component';
import {RoleRedirectGuard} from '@core/auth/role.guard';
import {AuthPageComponent} from '@features/auth/auth-page/auth-page.component';
import {CustomerPageComponent} from '@features/customer/customer-page/customer-page.component';

export const routes: Routes = [
  {
    path: '',
    canActivate: [RoleRedirectGuard],
    pathMatch: 'full',
    children: []
  },
  {
    path: '',
    component: LayoutComponent,
    canActivate: [RoleRedirectGuard],
    children: [
      {path: 'customers', component: CustomerPageComponent}
    ]
  },
  {path: 'auth', component: AuthPageComponent},
  {
    path: '**',
    canActivate: [RoleRedirectGuard],
    children: []
  }
];






