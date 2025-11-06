import {Injectable} from '@angular/core';
import {map} from 'rxjs/operators';
import {Observable} from 'rxjs';
import {BaseApiService} from '@api/services/base-api.service';
import {UserDomain} from '@core/models/user.model';
import {getUsers} from '@generated/users/users';
import {UserRoleDomain} from '@core/models/userRole.model';
import { plainToInstance } from 'class-transformer';
import { User as UserDto } from '@generated/models';

const {listAllUsers, getUserDetails, changeUserRole} = getUsers();

/**
 * Facade for user-related API calls.
 * Handles listing users, getting user details, and changing user roles.
 */
@Injectable({providedIn: 'root'})
export class UsersApiService extends BaseApiService {

  /** List all users.
   * @return Array of UserDomain
   * */
  listAll(): Observable<UserDomain[]> {
    return this.wrapPromise(listAllUsers()).pipe(
      map((users: UserDto[]) =>
        plainToInstance(UserDomain, users, {
          excludeExtraneousValues: true,
        })
      )
    );
  }

  /** Get user details by ID.
   *
   * @param id User ID.
   * @return UserDomain
   */
  detail(id: string): Observable<UserDomain> {
    return this.wrapPromise(getUserDetails(id)).pipe(
      map((u: UserDto) =>
        plainToInstance(UserDomain, u, {
          excludeExtraneousValues: true,
        })
      )
    );
  }

  /** Change user role.
   * Allows changing the role of a user and optionally updating their languages. (ONLY FOR INITIALIZATION)
   * @param id User ID.
   * @param role New role to assign to the user.
   * @param languages (Optional) Array of language codes to set for the user.
   */
  changeRole(id: string, role: UserRoleDomain, languages?: string[]): Observable<void> {
    const body = {role, languages: languages ?? []};
    return this.wrapPromise(changeUserRole(id, body)).pipe(map(() => void 0));
  }
}
