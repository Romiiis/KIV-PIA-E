import { Injectable } from '@angular/core';
import { map } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { BaseApiService } from '@api/apiServices/base-api.service';
import { ProjectDomain } from '@core/models/project.model';
import { ProjectMapper } from '@api/mappers/project.mapper';
import { getProjectsWorkflow } from '@generated/projects-workflow/projects-workflow';
import {ProjectFeedbackRequest, UploadTranslatedContentBody} from '@generated/models';

const {
  uploadTranslatedContent,
  approveTranslatedContent,
  rejectTranslatedContent,
  closeProject,
} = getProjectsWorkflow();


@Injectable({ providedIn: 'root' })
export class ProjectsWorkflowApiService extends BaseApiService {

  /**
   * Upload translated content for a project.
   * @param id Project ID.
   * @param file Translated content file.
   * Returns updated ProjectDomain.
   */
  uploadTranslated(id: string, file: File): Observable<ProjectDomain> {
    const form = new FormData();
    form.append('file', file);

    return this.wrapPromise(uploadTranslatedContent(id, form as unknown as UploadTranslatedContentBody)).pipe(
      map((p) => ProjectMapper.mapApiProjectToDomain(p))
    );
  }

  /**
   * Approve translated content for a project.
   * @param id Project ID.
   * Returns updated ProjectDomain.
   */
  approveTranslated(id: string): Observable<ProjectDomain> {
    return this.wrapPromise(approveTranslatedContent(id)).pipe(
      map((p) => ProjectMapper.mapApiProjectToDomain(p))
    );
  }

  /**
   * Reject translated content for a project with feedback.
   * @param id Project ID.
   * @param feedback Feedback text for rejection.
   * Returns updated ProjectDomain.
   */
  rejectTranslated(id: string, feedback: string): Observable<ProjectDomain> {
    const body: ProjectFeedbackRequest = { text: feedback };
    return this.wrapPromise(rejectTranslatedContent(id, body)).pipe(
      map((p) => ProjectMapper.mapApiProjectToDomain(p))
    );
  }

  /**
   * Close a project.
   * @param id Project ID.
   * Returns updated ProjectDomain.
   */
  close(id: string): Observable<ProjectDomain> {
    return this.wrapPromise(closeProject(id)).pipe(
      map((p) => ProjectMapper.mapApiProjectToDomain(p))
    );
  }
}
