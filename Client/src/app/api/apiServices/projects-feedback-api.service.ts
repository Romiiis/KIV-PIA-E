import { Injectable } from '@angular/core';
import { map } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { BaseApiService } from '@api/apiServices/base-api.service';
import { getProjectsFeedback } from '@generated/projects-feedback/projects-feedback';
import { ProjectFeedback } from '@generated/models';
import {FeedbackDomain} from '@core/models/feedback.model';
import {ProjectFeedbackMapper} from '@api/mappers/project-feedback.mapper';

const { getProjectFeedback } = getProjectsFeedback();

/**
 * Fasáda pro práci s feedbackem projektů — čtení hodnocení překladu.
 */
@Injectable({ providedIn: 'root' })
export class ProjectsFeedbackApiService extends BaseApiService {
  /**
   * Načte feedback pro daný projekt (CUSTOMER, TRANSLATOR nebo ADMIN).
   */
  getFeedback(projectId: string): Observable<FeedbackDomain> {
    return this.wrapPromise(getProjectFeedback(projectId)).pipe(
      map((response) => ProjectFeedbackMapper.mapApiProjectFeedbackToDomain(response))
    );
  }
}
