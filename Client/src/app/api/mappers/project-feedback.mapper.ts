import {FeedbackDomain} from '@core/models/feedback.model';
import {ProjectFeedback} from '@generated/models';

/**
 * Mapper for project feedback between API and domain models.
 */
export class ProjectFeedbackMapper {

  /**
   * Map API user to domain user
   * @param apiFeedback API user
   */
  public static mapApiProjectFeedbackToDomain(apiFeedback: ProjectFeedback): FeedbackDomain {
    return {
      projectId: apiFeedback.projectId,
      text: apiFeedback.text,
      createdAt: apiFeedback.createdAt!.toString()
    }
  }


}
