import {Injectable} from '@angular/core';
import {map} from 'rxjs/operators';
import {Observable} from 'rxjs';
import {plainToInstance} from 'class-transformer';

import {BaseApiService} from '@api/services/base-api.service';
import {ProjectDomain} from '@core/models/project.model';
import {getProjects} from '@generated/projects/projects';
import type { Project as ProjectDTO } from '@generated/models/project';

const {
  createProject,
  listAllProjects,
  getProjectDetails,
  downloadTranslatedContent,
  downloadOriginalContent,
} = getProjects();


/**
 * Facade for project-related API calls using class-transformer for mapping.
 * Handles listing, detailing, creating projects and downloading content.
 *
 */
@Injectable({providedIn: 'root'})
export class ProjectsApiService extends BaseApiService {

  /**
   * List all projects.
   * Returns an array of ProjectDomain class instances.
   */
  listAll(): Observable<ProjectDomain[]> {
    return this.wrapPromise(listAllProjects()).pipe(
      map((response: ProjectDTO[]) =>
        plainToInstance(ProjectDomain, response, {
          excludeExtraneousValues: true,
        })
      )
    );
  }

  /**
   * Get project details by ID.
   * @param id  Project ID.
   * Returns a ProjectDomain class instance.
   */
  detail(id: string): Observable<ProjectDomain> {
    return this.wrapPromise(getProjectDetails(id)).pipe(
      map((response: ProjectDTO) =>
        plainToInstance(ProjectDomain, response, {
          excludeExtraneousValues: true,
        })
      )
    );
  }

  /**
   * Create a new project with specified language code and file.
   * @param languageCode Language code for the project.
   * @param file File to be uploaded for the project.
   * Returns a ProjectDomain class instance.
   */
  create(languageCode: string, file: File): Observable<ProjectDomain> {
    return this.wrapPromise(
      createProject({languageCode, content: file})
    ).pipe(
      map((apiProject: ProjectDTO) =>
        plainToInstance(ProjectDomain, apiProject, {
          excludeExtraneousValues: true,
        })
      )
    );
  }

  /**
   * Download the original content of a project by ID.
   * @param id Project ID.
   */
  downloadOriginal(id: string): Observable<Blob> {
    return this.wrapPromise(downloadOriginalContent(id));
  }

  /**
   * Download the translated content of a project by ID.
   * @param id Project ID.
   */
  downloadTranslated(id: string): Observable<Blob> {
    return this.wrapPromise(downloadTranslatedContent(id));
  }
}
