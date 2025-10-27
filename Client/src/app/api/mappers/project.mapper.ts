import {UserDomain} from '@core/models/user.model';
import {User} from '../../../generatedAPI';
import {EnumMapper} from './enum.mapper';

export class ProjectMapper {

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
      createdAt: apiUser.createdAt.toISOString(),
    }
  }


}
