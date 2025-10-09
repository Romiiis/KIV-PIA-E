import { NgModule, Optional, SkipSelf } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HTTP_INTERCEPTORS } from '@angular/common/http';

import { AuthService } from './auth/auth.service';
import { AuthGuard } from './auth/auth.guard';
import { ApiService } from './services/api.service';
import { NotificationService } from './services/notification.service';
import { ErrorInterceptor } from './interceptors/error.interceptor';

@NgModule({
  imports: [CommonModule],
  providers: [
    AuthService,
    ApiService,
    NotificationService,
    AuthGuard,
    { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true }
  ]
})
export class CoreModule {
  constructor(@Optional() @SkipSelf() parentModule: CoreModule) {
    if (parentModule) {
      throw new Error('CoreModule is already loaded. Import it in AppModule only.');
    }
  }
}
