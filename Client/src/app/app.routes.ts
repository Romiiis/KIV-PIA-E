import {Routes} from '@angular/router';
import {AuthPageComponent} from '@features/auth/auth-page/auth-page.component';
import {CustomerPageComponent} from '@features/customer/customer-page/customer-page.component';

export const routes: Routes = [
  { path: '', component: CustomerPageComponent }
];
