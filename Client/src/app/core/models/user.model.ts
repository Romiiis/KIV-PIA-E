import {UserRoleDomain} from '@core/models/userRole.model';

export interface UserDomain {
  id: string;
  name: string;
  emailAddress: string;
  role: UserRoleDomain
  languages?: string[];
  createdAt: string;
}
