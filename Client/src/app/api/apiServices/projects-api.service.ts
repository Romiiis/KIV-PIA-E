import { Injectable } from '@angular/core';
import { BaseApiService, ApiResult } from './base-api.service';
import {
  createProject,
  listAllProjects,
  getProjectDetails,
  downloadOriginalContent,
  downloadTranslatedContent,
} from '@generated/projects/projects';
import type {
  CreateProjectRequest,
  ListAllProjectsParams,
} from '@generated/model';
import { ProjectMapper } from '@api/mappers/project.mapper';
import { ProjectDomain } from '@core/models/project.model';

@Injectable({ providedIn: 'root' })
export class ProjectsApiService extends BaseApiService {
  /**
   * Creates a new translation project (CUSTOMER only)
   */
  async create(payload: CreateProjectRequest): Promise<ApiResult<ProjectDomain>> {
    try {
      const res = await createProject(payload, this.defaultOptions);

      if (res.status === 201 && res.data) {
        const mapped = ProjectMapper.mapApiProjectToDomain(res.data);
        return this.handleResponse<ProjectDomain>({ ...res, data: mapped }, [201]);
      }

      return this.handleResponse<ProjectDomain>(res, [201]);
    } catch (err: any) {
      return { ok: false, status: 500, error: err.message ?? 'Network error' };
    }
  }

  /**
   * Lists all projects (all roles can access, but only their own)
   */
  async getAll(params?: ListAllProjectsParams): Promise<ApiResult<ProjectDomain[]>> {
    try {
      const res = await listAllProjects(params, this.defaultOptions);

      if (res.status === 200 && Array.isArray(res.data)) {
        const mapped = res.data.map(ProjectMapper.mapApiProjectToDomain);
        return this.handleResponse<ProjectDomain[]>({ ...res, data: mapped }, [200]);
      }

      return this.handleResponse<ProjectDomain[]>({ ...res, data: [] }, [200]);
    } catch (err: any) {
      return { ok: false, status: 500, error: err.message ?? 'Network error' };
    }
  }

  /**
   * Get project details by ID
   */
  async getById(id: string): Promise<ApiResult<ProjectDomain>> {
    try {
      const res = await getProjectDetails(id, this.defaultOptions);

      if (res.status === 200 && res.data) {
        const mapped = ProjectMapper.mapApiProjectToDomain(res.data);
        return this.handleResponse<ProjectDomain>({ ...res, data: mapped }, [200]);
      }

      return this.handleResponse<ProjectDomain>(res, [200]);
    } catch (err: any) {
      return { ok: false, status: 500, error: err.message ?? 'Network error' };
    }
  }

  /**
   * Download original content file of a project
   */
  async downloadOriginal(id: string): Promise<ApiResult<Blob>> {
    try {
      const res = await downloadOriginalContent(id, this.defaultOptions);
      return this.handleResponse<Blob>(res, [200]);
    } catch (err: any) {
      return { ok: false, status: 500, error: err.message ?? 'Network error' };
    }
  }

  /**
   * Download translated content file of a project
   */
  async downloadTranslated(id: string): Promise<ApiResult<Blob>> {
    try {
      const res = await downloadTranslatedContent(id, this.defaultOptions);
      return this.handleResponse<Blob>(res, [200]);
    } catch (err: any) {
      return { ok: false, status: 500, error: err.message ?? 'Network error' };
    }
  }
}
