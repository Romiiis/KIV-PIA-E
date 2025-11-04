import {
  APP_INITIALIZER,
  ApplicationConfig, EnvironmentInjector,
  importProvidersFrom,
  provideBrowserGlobalErrorListeners,
  provideZoneChangeDetection
} from '@angular/core';
import {provideRouter} from '@angular/router';
import {NgOptimizedImage} from '@angular/common';


import {routes} from './app.routes';
import {HTTP_INTERCEPTORS, provideHttpClient, withInterceptorsFromDi} from '@angular/common/http';
import {provideAngularQuery} from '@tanstack/angular-query-experimental';
import {QueryClient} from '@tanstack/query-core';
import {AuthService} from '@core/auth/auth.service';
import {ToastrModule} from 'ngx-toastr';
import {provideAnimations} from '@angular/platform-browser/animations';
import {AuthInterceptor} from '@core/interceptors/auth.interceptor';
import {setupFetchInterceptor} from '@core/interceptors/fetch-interceptor';

export function initializeAuth(auth: AuthService) {
  return () => auth.initialize();
}

export const appConfig: ApplicationConfig = {

  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
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
    {
      provide: 'APP_BOOTSTRAP_LISTENER',
      multi: true,
      useFactory: (env: EnvironmentInjector) => () => {
        setupFetchInterceptor(env);
      },
      deps: [EnvironmentInjector],
    },


  ]
};
