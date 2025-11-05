import {UserDomain} from '@core/models/user.model';
import {EnumMapper} from './enum.mapper';
import {User} from 'generatedAPI/models/user';


/**
 * Mapper for User objects between API and Domain models.
 * Handles conversion of user data structures.
 */
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
