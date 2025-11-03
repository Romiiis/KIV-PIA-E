import { Injectable } from '@angular/core';
import { BaseApiService, ApiResult } from './base-api.service';
import { getProjectFeedback } from '@generated/projects-feedback/projects-feedback';
import type { ProjectFeedback } from '@generated/model';
import { ProjectFeedbackMapper } from '@api/mappers/project-feedback.mapper';
import { FeedbackDomain } from '@core/models/feedback.model';

@Injectable({ providedIn: 'root' })
export class ProjectFeedbackApiService extends BaseApiService {
  /**
   * Get feedback for a specific project
   * (available for all roles, restricted to authorized projects)
   */
  async get(projectId: string): Promise<ApiResult<FeedbackDomain>> {
    try {
      const res = await getProjectFeedback(projectId, this.defaultOptions);

      if (res.status === 200 && res.data) {
        const mapped = ProjectFeedbackMapper.mapApiProjectFeedbackToDomain(res.data);
        return this.handleResponse<FeedbackDomain>({ ...res, data: mapped }, [200]);
      }

      return this.handleResponse<FeedbackDomain>(res, [200]);
    } catch (err: any) {
      return { ok: false, status: 500, error: err.message ?? 'Network error' };
    }
  }
}
