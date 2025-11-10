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
import {HttpClient, provideHttpClient, withInterceptorsFromDi} from '@angular/common/http';
import {provideAngularQuery} from '@tanstack/angular-query-experimental';
import {QueryClient} from '@tanstack/query-core';
import {AuthManager} from '@core/auth/auth.manager';
import {ToastrModule} from 'ngx-toastr';
import {provideAnimations} from '@angular/platform-browser/animations';
import {provideTranslateService} from '@ngx-translate/core';
import {provideTranslateHttpLoader} from '@ngx-translate/http-loader';
export const appConfig: ApplicationConfig = {

  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZoneChangeDetection({eventCoalescing: true}),
    provideRouter(routes),
    importProvidersFrom(NgOptimizedImage),
    provideHttpClient(withInterceptorsFromDi()),
    provideAngularQuery(new QueryClient()),
    provideAnimations(),
    // app.config.ts
    {
      provide: APP_INITIALIZER,
      useFactory: (auth: AuthManager) => () => auth.init(),
      deps: [AuthManager],
      multi: true
    },

    importProvidersFrom(
      ToastrModule.forRoot({
        positionClass: 'toast-top-left',
        timeOut: 3000,
        progressBar: true,
        closeButton: true,
      })
    ),
    provideTranslateService({
      loader: provideTranslateHttpLoader({
        prefix: '/i18n/',
        suffix: '.json'
      }),
      fallbackLang: 'en',
      lang: 'cs'
    })


  ]
};
