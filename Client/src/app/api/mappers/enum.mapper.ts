import {UserRoleDomain} from '@core/models/userRole.model';
import {ProjectStatusDomain} from '@core/models/projectStatus.model';
import {ProjectState, UserRole} from '@generated/models';

/**
 * Mapper for enumerations between API and domain models
 */
export class EnumMapper {

  /**
   * Map API user role to domain user role
   * @param apiRole API user role
   */
  public static mapApiUserRoleToDomainRole(apiRole: UserRole): UserRoleDomain {
    switch (apiRole) {
      case UserRole.CUSTOMER:
        return UserRoleDomain.CUSTOMER;
      case UserRole.TRANSLATOR:
        return UserRoleDomain.TRANSLATOR;
      case UserRole.ADMINISTRATOR:
        return UserRoleDomain.ADMINISTRATOR;
    }
  }


  /**
   * Map API project status to domain project status
   * @param apiStatus API project status
   * @returns Domain project status
   */
  public static mapApiProjectStatusToDomainStatus(apiStatus: ProjectState): ProjectStatusDomain {
    switch (apiStatus) {
      case ProjectState.CREATED:
        return ProjectStatusDomain.CREATED;
      case ProjectState.ASSIGNED:
        return ProjectStatusDomain.ASSIGNED;
      case ProjectState.APPROVED:
        return ProjectStatusDomain.APPROVED;
      case ProjectState.COMPLETED:
        return ProjectStatusDomain.COMPLETED;
      case ProjectState.CLOSED:
        return ProjectStatusDomain.CLOSED;
    }
  }
}
