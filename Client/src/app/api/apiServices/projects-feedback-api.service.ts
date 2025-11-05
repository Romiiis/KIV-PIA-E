import {Injectable} from '@angular/core';
import {map} from 'rxjs/operators';
import {Observable} from 'rxjs';
import {BaseApiService} from '@api/apiServices/base-api.service';
import {getProjectsFeedback} from '@generated/projects-feedback/projects-feedback';
import {FeedbackDomain} from '@core/models/feedback.model';
import {ProjectFeedbackMapper} from '@api/mappers/project-feedback.mapper';

const {getProjectFeedback} = getProjectsFeedback();


/**
 * Facade for project feedback-related API calls.
 * Handles fetching feedback for projects.
 */
@Injectable({providedIn: 'root'})
export class ProjectsFeedbackApiService extends BaseApiService {

  /**
   * Get feedback for a project by its projectId.
   * @param projectId Project ID.
   */
  getFeedback(projectId: string): Observable<FeedbackDomain> {
    return this.wrapPromise(getProjectFeedback(projectId)).pipe(
      map((response) => ProjectFeedbackMapper.mapApiProjectFeedbackToDomain(response))
    );
  }
}
