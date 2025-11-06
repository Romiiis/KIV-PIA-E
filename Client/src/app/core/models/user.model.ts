import { Expose } from 'class-transformer';
import { UserRoleDomain } from '@core/models/userRole.model';

/**
 * Domain model for a user.
 */
export class UserDomain {
  @Expose()
  id!: string;

  @Expose()
  name!: string;

  @Expose()
  emailAddress!: string;

  @Expose()
  role!: UserRoleDomain;

  @Expose()
  languages!: string[];

  @Expose()
  createdAt!: string;
}
