import {ProjectStatusDomain} from '@core/models/projectStatus.model';
import {UserDomain} from '@core/models/user.model';

export interface ProjectDomain {
  id?: string;
  customer: UserDomain;
  translator?: UserDomain;
  originalFileName?: string;
  translatedFileName?: string;
  targetLanguage: string;
  status?: ProjectStatusDomain;
  createdAt?: string;
}
