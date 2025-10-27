import {ProjectStatusDomain} from '@core/models/projectStatus.model';

export interface Project {
  id?: string;
  customerId: string;
  translatorId?: string;
  sourceFileUrl: string;
  targetLanguage: string;
  status?: ProjectStatusDomain
}
