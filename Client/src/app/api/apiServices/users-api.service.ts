import { Injectable } from '@angular/core';
import { map } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { BaseApiService } from '@api/apiServices/base-api.service';
import { UserDomain } from '@core/models/user.model';
import { UserMapper } from '@api/mappers/user.mapper';
import { getUsers } from '@generated/users/users';
import { UserRoleDomain } from '@core/models/userRole.model';

const { listAllUsers, getUserDetails, changeUserRole } = getUsers();

/**
 * Fasáda pro práci s uživateli — listování, detaily, jazyky a role.
 */
@Injectable({ providedIn: 'root' })
export class UsersApiService extends BaseApiService {
  /** Načte všechny uživatele (jen ADMIN). */
  listAll(): Observable<UserDomain[]> {
    return this.wrapPromise(listAllUsers()).pipe(
      map((users) => users.map((u) => UserMapper.mapApiUserToUser(u)))
    );
  }

  /** Načte detail uživatele podle ID. */
  detail(id: string): Observable<UserDomain> {
    return this.wrapPromise(getUserDetails(id)).pipe(
      map((u) => UserMapper.mapApiUserToUser(u))
    );
  }

  /** Změní roli uživatele (používá se jen při inicializaci účtu). */
  changeRole(id: string, role: UserRoleDomain, languages?: string[]): Observable<void> {
    const body = { role, languages: languages ?? [] };
    return this.wrapPromise(changeUserRole(id, body)).pipe(map(() => void 0));
  }
}
