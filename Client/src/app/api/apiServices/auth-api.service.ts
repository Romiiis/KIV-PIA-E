import {Injectable} from '@angular/core';
import {map, switchMap} from 'rxjs/operators';
import {Observable} from 'rxjs';
import {BaseApiService} from './base-api.service';
import {UserDomain} from '@core/models/user.model';
import {UserMapper} from '@api/mappers/user.mapper';
import {getAuth} from '@generated/auth/auth';
import {getMe} from '@generated/me/me';

const {loginUser, registerUser, logoutUser, refreshToken} = getAuth();
const {getCurrentUser} = getMe();

/**
 * Facade for authentication-related API calls.
 * Handles login, registration, logout, token refresh, and fetching current user.
 */
@Injectable({providedIn: 'root'})
export class AuthApiService extends BaseApiService {

  /**
   * Log in user with email and password.
   * On success, calls /me and returns domain model UserDomain.
   * */
  login(email: string, password: string): Observable<UserDomain> {
    return this.wrapPromise(loginUser({emailAddress: email, password}))
      .pipe(switchMap(() => this.me()));
  }

  /**
   * Register a new user with name, email and password.
   * On success, calls /me and returns domain model UserDomain.
   * */
  register(name: string, email: string, password: string): Observable<UserDomain> {
    return this.wrapPromise(registerUser({name, emailAddress: email, password}))
      .pipe(switchMap(() => this.me()));
  }

  /**
   * Refresh authentication token.
   * */
  refreshToken(): Observable<void> {
    return this.wrapPromise(refreshToken()).pipe(map(() => void 0));
  }

  /**
   * Logout current user.
   */
  logout(): Observable<void> {
    return this.wrapPromise(logoutUser()).pipe(map(() => void 0));
  }

  /**
   * Get current logged in user.
   * Returns domain model UserDomain.
   */
  me(): Observable<UserDomain> {
    return this.wrapPromise(getCurrentUser()).pipe(
      map((response) => UserMapper.mapApiUserToUser(response))
    );
  }
}
