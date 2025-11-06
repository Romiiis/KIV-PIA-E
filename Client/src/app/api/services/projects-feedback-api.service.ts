import {Injectable} from '@angular/core';
import {map} from 'rxjs/operators';
import {Observable} from 'rxjs';
import {plainToInstance} from 'class-transformer';

import {BaseApiService} from '@api/services/base-api.service';
import {getProjectsFeedback} from '@generated/projects-feedback/projects-feedback';
import {FeedbackDomain} from '@core/models/feedback.model';
import {ProjectFeedback as FeedbackDTO} from '@generated/models';


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
      map((response: FeedbackDTO) =>
        plainToInstance(FeedbackDomain, response, {
          excludeExtraneousValues: true,
        })
      )
    );
  }
}
