import {Injectable} from '@angular/core';
import {map} from 'rxjs/operators';
import {Observable} from 'rxjs';
import {BaseApiService} from '@api/apiServices/base-api.service';
import {ProjectDomain} from '@core/models/project.model';
import {ProjectMapper} from '@api/mappers/project.mapper';
import {getProjects} from '@generated/projects/projects';

const {
  createProject,
  listAllProjects,
  getProjectDetails,
  downloadTranslatedContent,
  downloadOriginalContent,
} = getProjects();


/**
 * Facade for project-related API calls.
 * Handles listing, detailing, creating projects and downloading content.
 *
 */
@Injectable({providedIn: 'root'})
export class ProjectsApiService extends BaseApiService {

  /**
   * List all projects.
   * Returns an array of ProjectDomain.
   */
  listAll(): Observable<ProjectDomain[]> {
    return this.wrapPromise(listAllProjects()).pipe(
      map((response) =>
        response.map((p) => ProjectMapper.mapApiProjectToDomain(p))
      )
    );
  }

  /**
   * Get project details by ID.
   * @param id  Project ID.
   * Returns a ProjectDomain.
   */
  detail(id: string): Observable<ProjectDomain> {
    return this.wrapPromise(getProjectDetails(id)).pipe(
      map((response) => ProjectMapper.mapApiProjectToDomain(response))
    );
  }

  /**
   * Create a new project with specified language code and file.
   * @param languageCode Language code for the project.
   * @param file File to be uploaded for the project.
   */
  create(languageCode: string, file: File): Observable<ProjectDomain> {
    return this.wrapPromise(
      createProject({languageCode, content: file})
    ).pipe(map((apiProject) => ProjectMapper.mapApiProjectToDomain(apiProject)));
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
