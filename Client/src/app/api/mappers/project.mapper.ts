import {EnumMapper} from './enum.mapper';
import {ProjectDomain} from '@core/models/project.model';
import {Project} from '@generated/model';
import {UserMapper} from './user.mapper';
import {UserRoleDomain} from '@core/models/userRole.model';

export class ProjectMapper {

  /**
   * Map API user to domain user
   * @param apiProject API user
   */
  public static mapApiProjectToDomain(apiProject: Project): ProjectDomain {
    return {
      id: apiProject.id,
      customer: UserMapper.mapApiUserToUser(apiProject.customer),
      translator: UserMapper.mapApiUserToUser(apiProject.translator!) || undefined,
      originalFileName: apiProject.originalFileName,
      translatedFileName: apiProject.translatedFileName,
      targetLanguage: apiProject.targetLanguage,
      status: EnumMapper.mapApiProjectStatusToDomainStatus(apiProject.state),
      createdAt: apiProject.createdAt.toString()
    }
  }


}
