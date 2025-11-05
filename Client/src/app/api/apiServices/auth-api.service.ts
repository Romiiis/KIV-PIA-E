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
 * Fasáda pro autentizační API — přihlášení, registrace, odhlášení, zjištění aktuálního uživatele.
 */
@Injectable({providedIn: 'root'})
export class AuthApiService extends BaseApiService {

  /**
   * Přihlášení uživatele podle e-mailu a hesla.
   * Po úspěchu volá /me a vrací domain model UserDomain.
   */
  login(email: string, password: string): Observable<UserDomain> {
    return this.wrapPromise(loginUser({emailAddress: email, password}))
      .pipe(switchMap(() => this.me()));
  }

  /**
   * Registrace nového uživatele (customer/translator).
   * Po úspěchu volá /me a vrací domain model UserDomain.
   */
  register(name: string, email: string, password: string): Observable<UserDomain> {
    return this.wrapPromise(registerUser({name, emailAddress: email, password}))
      .pipe(switchMap(() => this.me()));
  }

  refreshToken(): Observable<void> {
    return this.wrapPromise(refreshToken()).pipe(map(() => void 0));
  }

  /**
   * Odhlášení aktuálního uživatele — smaže session cookies na backendu.
   */
  logout(): Observable<void> {
    return this.wrapPromise(logoutUser()).pipe(map(() => void 0));
  }

  /**
   * Vrátí aktuálně přihlášeného uživatele na základě cookie session.
   */
  me(): Observable<UserDomain> {
    return this.wrapPromise(getCurrentUser()).pipe(
      map((response) => UserMapper.mapApiUserToUser(response))
    );
  }
}
