import {Routes} from '@angular/router';
import {LayoutComponent} from './layout/layout.component';
import {RoleRedirectGuard} from '@core/auth/role.guard';
import {AuthPageComponent} from '@features/auth/auth-page/auth-page.component';
import {CustomerPageComponent} from '@features/customer/customer-page/customer-page.component';
import {TranslatorPageComponent} from '@features/translator/translator-page/translator-page.component';
import {AdminPageComponent} from '@features/admin/admin-page/admin-page.component';
import {InitUserComponent} from '@features/init-user/init-user.component';

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
      {path: 'customer', component: CustomerPageComponent},
      {path: 'translator', component: TranslatorPageComponent},
      {path: 'admin', component: AdminPageComponent},
    ]
  },
  {path: "init", canActivate: [RoleRedirectGuard], component: InitUserComponent},
  {path: 'auth', canActivate: [RoleRedirectGuard], component: AuthPageComponent},
  {
    path: '**',
    canActivate: [RoleRedirectGuard],
    children: []
  }
];






