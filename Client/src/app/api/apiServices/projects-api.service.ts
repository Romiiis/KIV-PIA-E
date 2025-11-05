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
 * Fasáda pro práci s projekty — CRUD, upload/download souborů.
 */
@Injectable({providedIn: 'root'})
export class ProjectsApiService extends BaseApiService {

  /** Vrátí všechny projekty, podle oprávnění přihlášeného uživatele. */
  listAll(): Observable<ProjectDomain[]> {
    return this.wrapPromise(listAllProjects()).pipe(
      map((response) =>
        response.map((p) => ProjectMapper.mapApiProjectToDomain(p))
      )
    );
  }

  /** Vrátí detail projektu podle ID. */
  detail(id: string): Observable<ProjectDomain> {
    return this.wrapPromise(getProjectDetails(id)).pipe(
      map((response) => ProjectMapper.mapApiProjectToDomain(response))
    );
  }

  /**
   * Vytvoří nový projekt (CUSTOMER)
   * @param languageCode cílový jazyk (např. "de")
   * @param file obsah překladu jako File
   */
  create(languageCode: string, file: File): Observable<ProjectDomain> {
    return this.wrapPromise(
      createProject({languageCode, content: file})
    ).pipe(map((apiProject) => ProjectMapper.mapApiProjectToDomain(apiProject)));
  }

  /** Stáhne originální soubor projektu. */
  downloadOriginal(id: string): Observable<Blob> {
    return this.wrapPromise(downloadOriginalContent(id));
  }

  /** Stáhne přeložený soubor projektu. */
  downloadTranslated(id: string): Observable<Blob> {
    return this.wrapPromise(downloadTranslatedContent(id));
  }
}
