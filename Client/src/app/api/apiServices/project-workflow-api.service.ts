import {Injectable} from '@angular/core';
import {ApiResult, BaseApiService} from './base-api.service';
import {
  approveTranslatedContent,
  closeProject,
  rejectTranslatedContent,
  uploadTranslatedContent,
} from '@generated/projects-workflow/projects-workflow';
import type {ProjectFeedbackRequest, UploadTranslatedContentBody,} from '@generated/model';
import {ProjectMapper} from '@api/mappers/project.mapper';
import {ProjectDomain} from '@core/models/project.model';

@Injectable({providedIn: 'root'})
export class ProjectWorkflowApiService extends BaseApiService {
  /**
   * Upload translated file (only assigned translator can perform)
   */
  async uploadTranslatedContent(
    projectId: string,
    payload: UploadTranslatedContentBody
  ): Promise<ApiResult<ProjectDomain>> {
    try {
      const res = await uploadTranslatedContent(projectId, payload, this.defaultOptions);

      if (res.status === 200 && res.data) {
        const mapped = ProjectMapper.mapApiProjectToDomain(res.data);
        return this.handleResponse<ProjectDomain>({...res, data: mapped}, [200]);
      }

      return this.handleResponse<ProjectDomain>(res, [200]);
    } catch (err: any) {
      return {ok: false, status: 500, error: err.message ?? 'Network error'};
    }
  }

  /**
   * Approve translated content (only project customer can perform)
   */
  async approve(projectId: string): Promise<ApiResult<ProjectDomain>> {
    try {
      const res = await approveTranslatedContent(projectId, this.defaultOptions);

      if (res.status === 200 && res.data) {
        const mapped = ProjectMapper.mapApiProjectToDomain(res.data);
        return this.handleResponse<ProjectDomain>({...res, data: mapped}, [200]);
      }

      return this.handleResponse<ProjectDomain>(res, [200]);
    } catch (err: any) {
      return {ok: false, status: 500, error: err.message ?? 'Network error'};
    }
  }

  /**
   * Reject translated content (only project customer can perform)
   */
  async reject(
    projectId: string,
    feedback: ProjectFeedbackRequest
  ): Promise<ApiResult<ProjectDomain>> {
    try {
      const res = await rejectTranslatedContent(projectId, feedback, this.defaultOptions);

      if (res.status === 200 && res.data) {
        const mapped = ProjectMapper.mapApiProjectToDomain(res.data);
        return this.handleResponse<ProjectDomain>({...res, data: mapped}, [200]);
      }

      return this.handleResponse<ProjectDomain>(res, [200]);
    } catch (err: any) {
      return {ok: false, status: 500, error: err.message ?? 'Network error'};
    }
  }

  /**
   * Close project (only admin can perform)
   */
  async close(projectId: string): Promise<ApiResult<ProjectDomain>> {
    try {
      const res = await closeProject(projectId, this.defaultOptions);

      if (res.status === 200 && res.data) {
        const mapped = ProjectMapper.mapApiProjectToDomain(res.data);
        return this.handleResponse<ProjectDomain>({...res, data: mapped}, [200]);
      }

      return this.handleResponse<ProjectDomain>(res, [200]);
    } catch (err: any) {
      return {ok: false, status: 500, error: err.message ?? 'Network error'};
    }
  }
}
