import {Injectable} from '@angular/core';
import {UserDomain} from '@core/models/user.model';
import {UserMapper} from '../mappers/user.mapper';
import {map, Observable} from 'rxjs';
import {MeService} from '../../../generatedAPI';

@Injectable({ providedIn: 'root' })
export class meServiceWrapper {

  constructor(private service: MeService ) {

  }

  /**
   * Get the current logged in user
   */
  public getMe(): Observable<UserDomain> {
    return this.service.getCurrentUser().pipe(
      map(apiUser => UserMapper.mapApiUserToUser(apiUser))
    );
  }














}
