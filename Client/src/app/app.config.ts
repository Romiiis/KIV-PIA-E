import {
  APP_INITIALIZER,
  ApplicationConfig,
  importProvidersFrom,
  provideBrowserGlobalErrorListeners,
  provideZoneChangeDetection
} from '@angular/core';
import {provideRouter} from '@angular/router';
import {NgOptimizedImage} from '@angular/common';


import {routes} from './app.routes';
import {provideHttpClient, withInterceptorsFromDi} from '@angular/common/http';
import {provideAngularQuery} from '@tanstack/angular-query-experimental';
import {QueryClient} from '@tanstack/query-core';
import {AuthService} from '@core/auth/auth.service';
import {ToastrModule} from 'ngx-toastr';
import {provideAnimations} from '@angular/platform-browser/animations';

export function initializeAuth(auth: AuthService) {
  return () => auth.checkAndRestoreSession();
}

export const appConfig: ApplicationConfig = {

  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZoneChangeDetection({eventCoalescing: true}),
    provideRouter(routes),
    importProvidersFrom(NgOptimizedImage),
    provideHttpClient(withInterceptorsFromDi()),
    provideAngularQuery(new QueryClient()),
    {
      provide: APP_INITIALIZER,
      useFactory: initializeAuth,
      deps: [AuthService],
      multi: true
    },
    provideAnimations(),
    importProvidersFrom(
      ToastrModule.forRoot({
        positionClass: 'toast-top-right',
        timeOut: 3000,
        progressBar: true,
        closeButton: true,
      })
    ),


  ]
};
