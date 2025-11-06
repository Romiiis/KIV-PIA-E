import {Expose, Type} from 'class-transformer';
import {ProjectStatusDomain} from '@core/models/projectStatus.model';
import {UserDomain} from '@core/models/user.model';

/**
 * Domain model for a translation project.
 */
export class ProjectDomain {
  @Expose()
  id!: string;

  @Expose()
  @Type(() => UserDomain)
  customer!: UserDomain;

  @Expose()
  @Type(() => UserDomain)
  translator!: UserDomain | null;

  @Expose()
  originalFileName!: string;

  @Expose()
  translatedFileName!: string | null;

  @Expose()
  targetLanguage!: string;

  @Expose({name: 'state'})
  status!: ProjectStatusDomain;

  @Expose()
  createdAt!: string;
}
