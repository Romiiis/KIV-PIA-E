import {UserDomain} from '@core/models/user.model';
import {UserRoleDomain} from '@core/models/userRole.model';
import {EnumMapper} from './enum.mapper';
import {User} from 'generatedAPI/models/user';

export class UserMapper {



  /**
   * Map API user to domain user
   * @param apiUser API user
   */
  public static mapApiUserToUser(apiUser: User): UserDomain {
    return {
      id: apiUser.id,
      name: apiUser.name,
      emailAddress: apiUser.emailAddress,
      languages: apiUser.languages,
      role: EnumMapper.mapApiUserRoleToDomainRole(apiUser.role),
      createdAt: apiUser.createdAt.toString()
    }
  }



}
