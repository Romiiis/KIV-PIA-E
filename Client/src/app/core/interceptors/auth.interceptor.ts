import { Injectable } from '@angular/core';
import {
  HttpInterceptor,
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError, from } from 'rxjs';
import { catchError, switchMap } from 'rxjs/operators';
import { AuthApiService } from '@api/apiServices/auth-api.service';
import { AuthService } from '@core/auth/auth.service';
import { Router } from '@angular/router';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  private isRefreshing = false;

  constructor(
    private authApi: AuthApiService,
    private auth: AuthService,
    private router: Router
  ) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

    return next.handle(req).pipe(
      catchError((error) => {
        if (error instanceof HttpErrorResponse && error.status === 401 && !this.isRefreshing) {
          this.isRefreshing = true;
          console.warn('[AuthInterceptor] Access token expired, attempting refresh...');

          return from(this.authApi.refresh()).pipe(
            switchMap((res) => {
              this.isRefreshing = false;

              if (res.ok) {
                console.log('[AuthInterceptor] Token refreshed, retrying original request...');
                return next.handle(req);
              } else {
                console.error('[AuthInterceptor] Refresh failed, logging out...');
                return from(this.handleLogoutAndRedirect()).pipe(
                  switchMap(() => throwError(() => error))
                );
              }
            }),
            catchError((refreshErr) => {
              this.isRefreshing = false;
              console.error('[AuthInterceptor] Refresh exception', refreshErr);
              return from(this.handleLogoutAndRedirect()).pipe(
                switchMap(() => throwError(() => refreshErr))
              );
            })
          );
        }

        return throwError(() => error);
      })
    );
  }

  private async handleLogoutAndRedirect(): Promise<void> {
    await this.auth.logout();
    await this.router.navigate(['/auth']);
  }
}
