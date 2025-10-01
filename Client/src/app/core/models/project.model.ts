export interface Project {
  id?: string;
  customerId: string;
  translatorId?: string;
  sourceFileUrl: string;
  targetLanguage: string;
  status?: 'created' | 'assigned' | 'completed' | 'approved' | 'rejected' | 'closed';
}
