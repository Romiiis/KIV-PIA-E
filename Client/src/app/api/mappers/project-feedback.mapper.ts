import {EnumMapper} from './enum.mapper';
import {ProjectDomain} from '@core/models/project.model';
import {Project, ProjectFeedback} from '@generated/model';
import {FeedbackDomain} from '@core/models/feedback.model';

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
